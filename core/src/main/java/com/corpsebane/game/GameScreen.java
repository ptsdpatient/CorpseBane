package com.corpsebane.game;
import static com.corpsebane.game.Methods.load;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameScreen implements Screen {
    public static Player player;
    public CorpseBane game;
    public SpriteBatch batch;
    Vector3 touch;
    Vector2 point;
    float playerControlDelay=0f,playerFireDelay=0f;
    public BitmapFont font;
    boolean startSelected=false,endSelected=false;
    public static PathFinder pathFinder;
    public static OrthographicCamera camera;
    public static Vector2 screen=new Vector2(1280/2f,720/2f);
    public ShapeRenderer shapeRenderer;
    public Vector2 cellSize;
    public static GameCell[] gameCells;
    public Viewport viewport;
    static int gridScale=2;
    float playerSpeed= 0.375f,fireRate=0.55f;
    static int ROWS=22*gridScale,COLS=40*gridScale;
    public static Array<Projectile> projectiles;
    public static Array<Enemy> enemies;
    public static Array<NPC> peoples;
    public static Array<Puddle> puddles;
    public static Array<Merc> mercenaries;
    public static Array<Memo> memos;
    public static Array<Item> items;
    public static Array<Utility> utilities;
    Array<Dungeon> dungeons;
    static Rectangle gameRect;

    public static int randomX=0;
    public static int randomY=0;

    public static int mobKills=0,npcKills=0;

    float lightRevealTimer = 0;
    float revealDelay = 0.2f;
    int maxRadius = 4;

    public GameScreen(CorpseBane game){
        touch=new Vector3();

        this.game=game;
        this.batch=game.batch;

        player = new Player("player");

        projectiles=new Array<>();
        enemies=new Array<>();
        dungeons = new Array<>();
        peoples = new Array<>();
        puddles=new Array<>();
        mercenaries=new Array<>();
        items=new Array<>();
        memos=new Array<>();
        pathFinder=new PathFinder();

        font=new BitmapFont(load("font.fnt"));
        font.getData().setScale(0.13f);


        gameRect = new Rectangle(0, 0, COLS, ROWS);

        setWindowed();

        shapeRenderer=new ShapeRenderer();
        shapeRenderer.setColor(Color.WHITE);

        cellSize=new Vector2();
        cellSize.x = (float) Gdx.graphics.getWidth() / COLS;
        cellSize.y = (float) Gdx.graphics.getHeight() / ROWS;

        initializeCells();

        generateWorld();

//        camera.zoom=0.275f;

    }


    public static float getRandomDirection(){
        float rotation;
        Vector2 difference = getRandomCellPath().cpy().sub(getRandomCellPath());

        if (Math.abs(difference.x) > Math.abs(difference.y)) {
            if (difference.x > 0) {
                rotation=0f;
            } else {
                rotation=180;
            }
        } else {
            if (difference.y > 0) {
                rotation=90;
            } else {
                rotation=-90;
            }
        }
        return rotation;
    }

    public static boolean isNearby(Vector2 position1, Vector2 position2, float radius) {
        return position1.dst(position2) <= radius;
    }


    private void generateWorld() {
        int dungeonCount = MathUtils.random(3, 15);


        boolean touchingOtherRect;

        for (int i = 0; i < dungeonCount; i++) {
            Rectangle testRect = new Rectangle(MathUtils.random(0, COLS), MathUtils.random(0, ROWS), MathUtils.random(4, 15), MathUtils.random(4, 15));

            while (true) {
                touchingOtherRect = false;

                for (Dungeon rect : dungeons) {
                    if (testRect.overlaps(rect.dungeon)) {
                        touchingOtherRect = true;
                        break;
                    }
                }

                if (!gameRect.contains(testRect) || touchingOtherRect) {
                    testRect.set(MathUtils.random(0, COLS), MathUtils.random(0, ROWS), MathUtils.random(4, 15), MathUtils.random(4, 15));
                } else {
                    dungeons.add(new Dungeon(testRect));
                    break;
                }
            }
        }

        print("dungeon count is : " + dungeons.size);


        for (Dungeon dungeon : dungeons) {
            Rectangle rect = dungeon.dungeon;
            int z= MathUtils.random(1,4);
            for (int i = (int) rect.y; i < rect.y + rect.height; i++) {
                for (int j = (int) rect.x; j < rect.x + rect.width; j++) {
                    if (i == rect.y || i == rect.y + rect.height - 1 || j == rect.x || j == rect.x + rect.width - 1) {
                        int index = getCellIndex(i, j);
                        if (index >= 0 && index < gameCells.length) {
                            boolean isCorner =
                                (i == rect.y && j == rect.x) ||
                                    (i == rect.y && j == rect.x + rect.width - 1) ||
                                    (i == rect.y + rect.height - 1 && j == rect.x) ||
                                    (i == rect.y + rect.height - 1 && j == rect.x + rect.width - 1);



                            gameCells[index].isBorder = true;
                        }

                    }else gameCells[getCellIndex(i, j)].isRoad=true;
                }
            }
        }
        int connectDoorCount=0;
        while(connectDoorCount<dungeons.size){
            for(Dungeon dungeon : dungeons){
                if(!dungeon.isConnected){
                    print("connecting door ");
                    connectDoorCount++;
                    pathFinder.connectDoor(getRandomCellInRectangle(dungeon.dungeon),getRandomCellInRectangle(dungeons.random().dungeon));
                    dungeon.isConnected=true;
                }
            }
        }


        player.setPosition(getRandomCellInRectangle(dungeons.random().dungeon));
//        for(int l=0;l<7;l++)enemies.add(new Enemy(3,getRandomCellPath(),getRandomDirection()));
        for(int l=0;l<15;l++)peoples.add(new NPC(MathUtils.random(0,2),getRandomCellPath(),getRandomDirection()));
//        for(int l=0;l<4;l++)mercenaries.add(new Merc(MathUtils.random(0,1)==0,getRandomCellPath(),getRandomDirection()));
        for(int l=0;l<20;l++)items.add(new Item(MathUtils.random(0,21),getRandomCellPath()));
        for(int l=0;l<15;l++)memos.add(new Memo(MathUtils.random(0,2),getRandomCellPath()));
        for(int l=0;l<15;l++)utilities.add(new Utility(MathUtils.randomBoolean(),getRandomCellPath()));


    }

    public static int getCellIndex(int i, int j) {
        return i * COLS + j;
    }

    public static Vector2 getRandomCellPath(){
        Vector2 randomPoint;
        while(true){
            randomPoint=getRandomCellInRectangle(gameRect);
            if(gameCells[getCellIndex((int) randomPoint.y, (int) randomPoint.x)].isRoad &&!gameCells[getCellIndex((int) randomPoint.y, (int) randomPoint.x)].isBorder){
                return randomPoint;
            }
        }
    }

    public static Vector2 getRandomCellInRectangle(Rectangle rect) {
        randomX = MathUtils.random((int) rect.x+1, (int) (rect.x + rect.width - 2));
        randomY = MathUtils.random((int) rect.y+1, (int) (rect.y + rect.height - 2));
        return new Vector2(randomX, randomY);
    }


    private void setWindowed(){
        camera=new OrthographicCamera();
        screen=new Vector2(1280/2f,720/2f);
        viewport=new ExtendViewport(screen.x, screen.y,camera);
        camera.setToOrtho(false, screen.x, screen.y);
        viewport.apply();
    }
    private void setFullScreen(){
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        screen=new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera=new OrthographicCamera();
        viewport=new ExtendViewport(screen.x, screen.y,camera);
        camera.setToOrtho(false, screen.x, screen.y);
        viewport.apply();
    }

    public static boolean isPlayerBad(){
        return mobKills-npcKills<0;
    }

    private void initializeCells() {
        gameCells=new GameCell[ROWS*COLS];
        int index=0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                gameCells[index]=new GameCell(col,row,index);
                index++;
            }
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        litDungeon(delta);

        revealPath();

        camera.position.set(screen.x/2f,screen.y/2f,0);

//        camera.position.set(player.obj.getX()+player.playerSize.x/2f,player.obj.getY()+player.playerSize.y/2f,0);
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(GameCell cell : gameCells){

            shapeRenderer.setColor(Color.BLACK);
//            if(cell.isEnd)shapeRenderer.setColor(Color.RED);
//            if(cell.isStart||cell.isExplored)shapeRenderer.setColor(Color.GREEN);
            if(cell.isBorder)shapeRenderer.setColor(Color.LIGHT_GRAY);
            if(cell.isRoad)shapeRenderer.setColor(Color.DARK_GRAY);

//          if(cell.isPath){
            shapeRenderer.rect(cell.i*cellSize.x, cell.j*cellSize.y, cellSize.x, cellSize.y);
//          }

        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for(GameCell cell : gameCells){
            if(cell.isRoad||cell.isBorder)
                shapeRenderer.rect(cell.i*cellSize.x, cell.j*cellSize.y, cellSize.x, cellSize.y);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(Item item : items){
//            if(gameCells[getCellIndex((int) puddle.coordinates.y, (int) puddle.coordinates.x)].isPath){
                item.render(batch);
//            }
        }

        for(Memo memo : memos){
//            if(gameCells[getCellIndex((int) puddle.coordinates.y, (int) puddle.coordinates.x)].isPath){
                memo.render(batch);
//            }
        }


        for(Puddle puddle : puddles){
//            if(gameCells[getCellIndex((int) puddle.coordinates.y, (int) puddle.coordinates.x)].isPath){
                puddle.render(batch);
//            }
        }

        for(Utility util : utilities){
//            if(gameCells[getCellIndex((int) puddle.coordinates.y, (int) puddle.coordinates.x)].isPath){
                util.render(batch);
//            }
        }

        for(NPC npc : peoples){
//            if(gameCells[getCellIndex((int) npc.coordinates.y, (int) npc.coordinates.x)].isPath){
                npc.render(batch,delta);
                if(npc.health<1){
                    if(MathUtils.random(2,17)%6==3)
                        enemies.add(new Enemy(4,npc.coordinates,npc.obj.getRotation()));
                    else
                        puddles.add(new Puddle(0,new Vector2(npc.coordinates),npc.obj.getRotation()));
                    peoples.removeValue(npc,true);
                }
//            }
        }


        for(Merc merc : mercenaries){
//            if(gameCells[getCellIndex((int) merc.coordinates.y, (int) merc.coordinates.x)].isPath) {

                merc.render(batch, delta);
                if(merc.health<1){
                    puddles.add(new Puddle(merc.bad?3:4,new Vector2(merc.coordinates),merc.obj.getRotation()));
                    mercenaries.removeValue(merc,true);
                }
//            }
        }


        for(Enemy enemy : enemies){
//            if(gameCells[getCellIndex((int) enemy.coordinates.y, (int) enemy.coordinates.x)].isPath){
                enemy.render(batch,delta);
                if(enemy.health<1){
                    puddles.add(new Puddle(enemy.type<4?1:enemy.type==4?5:6,new Vector2(enemy.coordinates),enemy.obj.getRotation()));
                    enemies.removeValue(enemy,true);
                }
//            }
        }



        for(Projectile projectile : projectiles){
            projectile.render(batch,delta);
            if(projectile.isDead){
                projectiles.removeValue(projectile,true);
            }

        }

        font.draw(batch,"Health "+(int) player.health,player.obj.getX()-player.playerSize.x*10f,player.obj.getY()+player.playerSize.y*6.325f);
        font.draw(batch,"Sub level "+ player.subLevel,player.obj.getX()-player.playerSize.x,player.obj.getY()+player.playerSize.y*6.325f);
        font.draw(batch,(player.rifle?"Rifle ":"Pistol ")+ (int) player.ammo,player.obj.getX()+player.playerSize.x*6.85f,player.obj.getY()+player.playerSize.y*6.325f);


        player.render(batch);

        player.obj.setRegion(player.playerSheet[Gdx.input.isKeyPressed(Input.Keys.SPACE)||Gdx.input.isKeyPressed(Input.Buttons.RIGHT)?player.rifle?3:1:0]);

        if(playerFireDelay>(player.rifle?0.2f:0.55f)&&(Gdx.input.isKeyPressed(Input.Keys.SPACE)||Gdx.input.isKeyPressed(Input.Buttons.RIGHT))){
            handleFire();
        }else{
            playerFireDelay+=delta;
        }

        if(playerControlDelay>playerSpeed){
            controlPlayer();
        }else{
            playerControlDelay+=delta;
        }


        batch.end();

    }

    private void revealPath() {
        int playerX = (int) player.coordinates.x;
        int playerY = (int) player.coordinates.y;
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int checkX = playerX + offsetX;
                int checkY = playerY + offsetY;

                if (checkX >= 0 && checkX < COLS && checkY >= 0 && checkY < ROWS) {
                    int subIndex = getCellIndex(checkY, checkX);

                    if (gameCells[subIndex] != null || !gameCells[subIndex].isBorder ) {
                        gameCells[subIndex].isPath = true;
                    }
                }
            }
        }
    }

    private void litDungeon(float delta) {
        lightRevealTimer += delta;
        if (lightRevealTimer >= revealDelay) {
            lightRevealTimer = 0;

            for (Dungeon dungeon : dungeons) {
                if (dungeon.dungeon.contains(player.coordinates)) {
                    Rectangle rect = dungeon.dungeon;
                    int playerX = (int) player.coordinates.x;
                    int playerY = (int) player.coordinates.y;
                    for (int y = playerY - (int) dungeon.currentRadius; y <= playerY + (int) dungeon.currentRadius; y++) {
                        for (int x = playerX - (int) dungeon.currentRadius; x <= playerX + (int) dungeon.currentRadius; x++) {
                            if (x >= rect.x && x < rect.x + rect.width && y >= rect.y && y < rect.y + rect.height) {
                                int distanceSquared = (x - playerX) * (x - playerX) + (y - playerY) * (y - playerY);
                                if (distanceSquared <= dungeon.currentRadius * dungeon.currentRadius) {
                                    int index = getCellIndex(y, x);
                                    gameCells[index].isPath = true;
                                }
                            }
                        }
                    }
                    dungeon.currentRadius += 1;
                    if (dungeon.currentRadius > maxRadius) {
                        dungeon.currentRadius = maxRadius;
                    }
                }
            }
        }
    }

    private void handleFire() {
        if(Gdx.input.isKeyPressed(Input.Keys.Z)||Gdx.input.isKeyPressed(Input.Buttons.LEFT)) {
//            print("fire");
            if(player.ammo>1){
                projectiles.add(new Projectile(player.playerSheet[player.rifle?4:2], player.coordinates, player.obj.getRotation()));
                playerFireDelay = 0f;
                player.ammo--;
            }
        }
    }

    private void controlPlayer() {

        playerSpeed= (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))? (float) (0.375 / 2.5f) :0.375f;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=0){
                player.obj.setRotation(0);
                return;
            }else if(checkCollision(player.coordinates.x+1, player.coordinates.y)) {
                player.setPosition(new Vector2(player.coordinates.x+1, player.coordinates.y));
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=90){
                player.obj.setRotation(90);
                return;

            }else if(checkCollision(player.coordinates.x, player.coordinates.y+1)){
                player.setPosition(new Vector2(player.coordinates.x, player.coordinates.y+1));
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=180){
                player.obj.setRotation(180);
                return;

            }else  if(checkCollision(player.coordinates.x-1, player.coordinates.y)) {
                player.setPosition(new Vector2(player.coordinates.x-1, player.coordinates.y));
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=-90){
                player.obj.setRotation(-90);

            }else  if(checkCollision(player.coordinates.x, player.coordinates.y-1)) {
                player.setPosition(new Vector2(player.coordinates.x, player.coordinates.y-1));
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode==Input.Keys.X){
                    player.rifle=!player.rifle;
                }

                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
//                if(keycode==Input.Keys.SPACE && startSelected && endSelected){
//                    findPath();
//                }



                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point=new Vector2(touch.x,touch.y);

                for(GameCell cell : gameCells){
                    if(cell.isTouching(point)){
                        print("selected cell cell: (" + cell.i + ", " + cell.j + ")");
                        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
                            if(startSelected && cell.isStart){
                                startSelected=false;
                                cell.isStart=false;
                            }else if(!startSelected && !cell.isStart){
                                startSelected=true;
                                cell.isStart=true;
                            }
                        }
                        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
                            if(endSelected && cell.isEnd){
                                endSelected=false;
                                cell.isEnd=false;
                            }else if(!endSelected && !cell.isEnd){
                                endSelected=true;
                                cell.isEnd=true;
                            }
                        }




                    }



                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);

//                for(GameCell cell : gameCells){
//                    if(cell.isTouching(new Vector2(touch.x,touch.y))){
//                        cell.isActive=true;
//                    }
//                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);

                for(GameCell cell : gameCells){
                    cell.isHovered=cell.isTouching(new Vector2(touch.x,touch.y));
                }
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });
    }



    public static boolean checkCollision(float i, float j) {
        int index = getCellIndex((int) j, (int) i);
        return !gameCells[index].isBorder&&gameCells[index].isRoad || gameCells[index].isActive;
    }


    private void findPath() {
        Vector2 startCell=new Vector2(),endCell=new Vector2();
        for(GameCell cell : gameCells){
            if(cell.isStart)startCell=new Vector2(cell.i,cell.j);
            if(cell.isEnd)endCell=new Vector2(cell.i,cell.j);
        }
        print(""+pathFinder.findPath(startCell,endCell,15));
    }
}
