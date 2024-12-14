package com.corpsebane.game;
import static com.corpsebane.game.CorpseBane.pauseScreen;
import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.load;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameScreen implements Screen {
    public static Player player;
    public static Ladder ladder;
    public CorpseBane game;
    public SpriteBatch batch;
    Vector3 touch;
    Vector2 point;
    float playerControlDelay=0f,playerFireDelay=0f;
    public BitmapFont font;
    boolean startSelected=false,endSelected=false,showMessage=false;
    public static PathFinder pathFinder;
    public static OrthographicCamera camera;
    public static Vector2 screen=new Vector2(1280/2f,720/2f);
    public ShapeRenderer shapeRenderer;
    public Vector2 cellSize;
    public static GameCell[] gameCells;
    public Viewport viewport;
    static int gridScale=2,showMessageIndex=0;
    float playerSpeed= 0.375f;
    static int ROWS=22*gridScale,COLS=40*gridScale,exploredDungeon=0;
    public static Array<Projectile> projectiles;
    public boolean showInteractButton=false;

    public static Array<Spawn> spawns;
    public static Array<Enemy> enemies;
    public static Array<NPC> peoples;
    public static Array<Puddle> puddles;
    public static Array<Merc> mercenaries;
    public static Array<Memo> memos;
    public static Array<Item> items;
    public static Array<Vent> vents;
    public static Array<Button> buttons;

    String typewriterText="";

    public static Array<Sound> typewriter,walk,monsterWalk,monsterGrowl,npcSound;
    public static Sound fire,hurt,ladderSound,gameStartSound;

    public Music theme;

    public static String[] docs;
    public static int docsIndex=0;
    public static GlyphLayout glyphLayout;


    public static Array<Utility> utilities;
    public static Array<Dungeon> dungeons;
    static Rectangle gameRect;
    public String displayMessage="";

    public static int randomX=0;
    public static int randomY=0;

    public static int mobKills=0,npcKills=0;

    public static boolean ladderActive=false,moveUp=false,moveDown=false,moveLeft=false,moveRight=false,run=false,aim=false,shoot=false,toggle=false,showControls=true;

    float lightRevealTimer = 0;
    float revealDelay = 0.2f,showMessageDelay=0f,typeWriterSpeed=0.075f;
    int maxRadius = 4;

    public GameScreen(CorpseBane game){
        touch=new Vector3();

        player = new Player("player");

        this.game=game;
        this.batch=game.batch;

        spawns=new Array<>();
        buttons=new Array<>();


        typewriter=new Array<>();
        for(int i=0;i<3;i++){
            typewriter.add(Gdx.audio.newSound(load("typewriter/"+(i+1)+".wav")));
        }

        walk=new Array<>();
        for(int i=0;i<4;i++){
            walk.add(Gdx.audio.newSound(load("footstep/footstep"+(i+1)+".wav")));
        }

        monsterWalk = new Array<>();
        for(int i=0;i<2;i++){
            monsterWalk.add(Gdx.audio.newSound(load("monster/monsterfootstep"+(i+1)+".wav")));
        }

        monsterGrowl = new Array<>();
        for(int i=0;i<2;i++){
            monsterGrowl.add(Gdx.audio.newSound(load("monster/monstergrowl"+(i+1)+".wav")));
        }

        npcSound= new Array<>();
        for(int i=0;i<2;i++){
            npcSound.add(Gdx.audio.newSound(load("npc/speech"+(i+1)+".wav")));
        }

        fire=Gdx.audio.newSound(load("gun.wav"));
        hurt=Gdx.audio.newSound(load("hurt.wav"));
        ladderSound=Gdx.audio.newSound(load("ladder.wav"));
        gameStartSound=Gdx.audio.newSound(load("start_game.wav"));

        theme=Gdx.audio.newMusic(load("lone.wav"));
        theme.setLooping(true);
        theme.setVolume(2f);

        docs=Gdx.files.internal("docs.txt").readString().split("\\r?\\n");
        glyphLayout = new GlyphLayout();
        pathFinder=new PathFinder();
        font=new BitmapFont(load("font.fnt"));


        gameRect = new Rectangle(0, 0, COLS, ROWS);

        setWindowed();

        shapeRenderer=new ShapeRenderer();
        shapeRenderer.setColor(Color.WHITE);

        cellSize=new Vector2();
        cellSize.x = (float) Gdx.graphics.getWidth() / COLS;
        cellSize.y = (float) Gdx.graphics.getHeight() / ROWS;

        handleSpawns();
        addControls();
//        generateWorld();


        camera.zoom=0.275f;
        font.getData().setScale(0.13f);


    }

    private void addControls() {
        buttons.add(new Button(0,"movement"));
        buttons.add(new Button(1,"movement"));
        buttons.add(new Button(2,"movement"));
        buttons.add(new Button(3,"movement"));
        buttons.add(new Button(4,"run"));
        buttons.add(new Button(5,"toggle"));
        buttons.add(new Button(6,"shoot"));
        buttons.add(new Button(7,"exit"));
        buttons.add(new Button(8,"control"));
        buttons.add(new Button(9,"sound"));
        buttons.add(new Button(10,"aim"));

    }

    private void handleSpawns() {
        spawns.add(new Spawn(5,0,0,0,0,4,6,50,0,0));
        spawns.add(new Spawn(6,0,0,0,3,7,3,30,3,0));
        spawns.add(new Spawn(4,6,0,1,6,6,8,25,5,4));
        spawns.add(new Spawn(7,6,0,1,5,7,5,20,4,8));
        spawns.add(new Spawn(MathUtils.random(5,7),9,0,1,10,6,10,25,7,14));
        spawns.add(new Spawn(MathUtils.random(5,12),15,0,2,10,12,7,25,4,20));
        spawns.add(new Spawn(MathUtils.random(5,12),25,0,3,10,6,10,30,10,20));
        spawns.add(new Spawn(MathUtils.random(5,7),3,0,1,5,4,5,20,4,8));
        spawns.add(new Spawn(MathUtils.random(5,7),15,0,2,10,3,7,25,4,20));
        spawns.add(new Spawn(MathUtils.random(5,7),7,0,3,10,6,10,25,7,14));
        spawns.add(new Spawn(MathUtils.random(8,13),25,0,3,10,6,10,30,10,20));
        spawns.add(new Spawn(MathUtils.random(7,20),30,0,3,20,3,10,15,6,18));
        spawns.add(new Spawn(MathUtils.random(6,22),40,1,4,12,7,12,10,7,30));
        spawns.add(new Spawn(MathUtils.random(6,22),50,2,4,12,7,12,10,7,25));
        spawns.add(new Spawn(MathUtils.random(6,22),60,2,5,12,7,12,10,7,20));
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


    public void generateWorld() {
        player.health=100;
        player.ammo=0;

        initializeCells();

        projectiles=new Array<>();
        enemies=new Array<>();
        dungeons = new Array<>();
        peoples = new Array<>();
        puddles=new Array<>();
        mercenaries=new Array<>();
        items=new Array<>();
        memos=new Array<>();
        utilities=new Array<>();
        vents=new Array<>();

        int dungeonCount = spawns.get((player.subLevel*-1)-1).dungeonCount;


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
                    dungeons.add(new Dungeon(testRect,i));
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
                Dungeon randomDungeon = dungeons.random();
                if(!dungeon.isConnected&&randomDungeon.id!=dungeon.id){
                    connectDoorCount++;
                    pathFinder.connectDoor(getRandomCellInRectangle(dungeon.dungeon),getRandomCellInRectangle(randomDungeon.dungeon));
                    dungeon.isConnected=true;
                }
            }
        }

        for(Dungeon dungeon : dungeons){
            if(MathUtils.randomBoolean(0.5f)){
                pathFinder.connectDoor(getRandomCellInRectangle(dungeon.dungeon),getRandomCellInRectangle(dungeons.random().dungeon));
            }
        }


        player.setPosition(getRandomCellInRectangle(dungeons.random().dungeon));
        player.obj.setRotation(getRandomDirection());



        for(int l=0;l<spawns.get((player.subLevel*-1)-1).enemyCount;l++)enemies.add(new Enemy(MathUtils.random(spawns.get((player.subLevel*-1)-1).startRange,spawns.get((player.subLevel*-1)-1).endRange),getRandomCellPath(),getRandomDirection()));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).npcCount;l++)peoples.add(new NPC(MathUtils.random(0,2),getRandomCellPath(),MathUtils.randomBoolean(0.65f)));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).mercCount;l++)mercenaries.add(new Merc(MathUtils.random(0,1)==0,getRandomCellPath(),getRandomDirection()));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).itemCount;l++)items.add(new Item(MathUtils.random(0,21),getRandomCellPath()));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).memoCount;l++)memos.add(new Memo(MathUtils.random(0,2),getRandomCellPath()));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).utilityCount;l++)utilities.add(new Utility(MathUtils.randomBoolean(),getRandomCellPath()));
        for(int l=0;l<spawns.get((player.subLevel*-1)-1).ventCount;l++)vents.add(new Vent());
        ladderActive=false;
        ladder=null;

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
        return npcKills > 3 && mobKills > 3 && mobKills < (npcKills + 20);
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

        if(player.health<-10){
            game.setDiedScreen();
        }

        litDungeon(delta);

        for(Dungeon dungeon : dungeons){
            if(!dungeon.isExplored&& dungeon.dungeon.contains(player.coordinates)){
                dungeon.isExplored=true;
                exploredDungeon++;
            }
        }
        if(exploredDungeon==(dungeons.size-2)&&!ladderActive){
            print("spawn ladder!");
            for(Dungeon dungeon : dungeons){
                if(!dungeon.isExplored){
                    ladder=new Ladder(getRandomCellInRectangle(dungeon.dungeon));
                    ladderActive=true;
                }
            }
        }
        revealPath();

//        camera.position.set(screen.x/2f,screen.y/2f,0);
        camera.position.set(player.obj.getX()+player.playerSize.x/2f,player.obj.getY()+player.playerSize.y/2f,0);
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(GameCell cell : gameCells){

            shapeRenderer.setColor(Color.BLACK);
            if(cell.isBorder)shapeRenderer.setColor(Color.DARK_GRAY);
            if(cell.isRoad)shapeRenderer.setColor(Color.LIGHT_GRAY);
            if(cell.isPath){
                shapeRenderer.rect(cell.i*player.playerSize.x, cell.j*player.playerSize.y, player.playerSize.x, player.playerSize.y);
            }

        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for(GameCell cell : gameCells){
            if(cell.isRoad||cell.isBorder)
                if(cell.isPath)
                    shapeRenderer.rect(cell.i*player.playerSize.x, cell.j*player.playerSize.y, player.playerSize.x, player.playerSize.y);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(Vent vent : vents){
            vent.render(batch);
        }

        for(Item item : items){
            if(gameCells[getCellIndex((int) item.coordinates.y, (int) item.coordinates.x)].isPath){
                item.render(batch);
            }
        }

        for(Memo memo : memos){
            if(gameCells[getCellIndex((int) memo.coordinates.y, (int) memo.coordinates.x)].isPath){
                memo.render(batch);
            }
        }


        for(Puddle puddle : puddles){
            if(gameCells[getCellIndex((int) puddle.coordinates.y, (int) puddle.coordinates.x)].isPath){
                puddle.render(batch);
            }
        }


        for(Utility util : utilities){
            if(gameCells[getCellIndex((int) util.coordinates.y, (int) util.coordinates.x)].isPath){
                util.render(batch);
            }
        }


        for(NPC npc : peoples){
            if(gameCells[getCellIndex((int) npc.coordinates.y, (int) npc.coordinates.x)].isPath){
                npc.render(batch,delta);
                if(npc.health<1){
                    if(MathUtils.random(2,17)%6==3)
                        enemies.add(new Enemy(4,npc.coordinates,npc.obj.getRotation()));
                    else
                        puddles.add(new Puddle(0,new Vector2(npc.coordinates),npc.obj.getRotation()));
                    peoples.removeValue(npc,true);
                }
            }
        }


        for(Merc merc : mercenaries){
            if(gameCells[getCellIndex((int) merc.coordinates.y, (int) merc.coordinates.x)].isPath) {

                merc.render(batch, delta);
                if(merc.health<1){
                    puddles.add(new Puddle(merc.bad?3:4,new Vector2(merc.coordinates),merc.obj.getRotation()));
                    mercenaries.removeValue(merc,true);
                }
            }
        }


        for(Enemy enemy : enemies){
            if(gameCells[getCellIndex((int) enemy.coordinates.y, (int) enemy.coordinates.x)].isPath){
                enemy.render(batch,delta);
                if(enemy.health<1){
                    puddles.add(new Puddle(enemy.type<4?1:enemy.type==4?5:6,new Vector2(enemy.coordinates),enemy.obj.getRotation()));
                    enemies.removeValue(enemy,true);
                }
            }
        }



        for(Projectile projectile : projectiles){
            projectile.render(batch,delta);
            if(projectile.isDead){
                projectiles.removeValue(projectile,true);
            }
        }

        for(Button button : buttons){
            if(showControls||button.index==7||button.index==8){
                button.render(batch,new Vector2(player.obj.getX(),player.obj.getY()));

            }
        }

       if(ladderActive){
           if(gameCells[getCellIndex((int) ladder.coordinates.y, (int) ladder.coordinates.x)].isPath){
               ladder.render(batch);
           }
       }

//        if(player.subLevel<-2){
//            font.draw(batch,"Health "+(int) player.health,player.obj.getX()-player.playerSize.x*6f,player.obj.getY()+player.playerSize.y*6f);
//            font.draw(batch,"Sub level "+ player.subLevel,player.obj.getX()-player.playerSize.x,player.obj.getY()+player.playerSize.y*6);
//            font.draw(batch,(player.rifle?"Rifle ":"Pistol ")+ (int) player.ammo,player.obj.getX()+player.playerSize.x*4.5f,player.obj.getY()+player.playerSize.y*6f);
//        }


        if(!showMessage){
            showInteractButton=false;
            showMessage=checkInteract();
            if(showInteractButton){
               font.draw(batch,"Press Z to interact",player.obj.getX()-player.playerSize.x*2.5f,player.obj.getY()-player.playerSize.y*3);
            }
        }else if(displayMessage.length()>1){
            typewriterText=(showMessageIndex < displayMessage.length()) ? displayMessage.substring(0, showMessageIndex) : displayMessage;

            glyphLayout.setText(font,typewriterText, Color.WHITE, displayMessage.length()+35, Align.center, true);

            font.draw(
                batch,
                glyphLayout,
                player.obj.getX()-glyphLayout.width/2f+5f,
                player.obj.getY() + player.playerSize.y * 6f
//                player.obj.getY() - player.playerSize.y * 1.75f
            );

            if (showMessageDelay > typeWriterSpeed) {
                // Draw the current substring of the message

                if(showMessageIndex < displayMessage.length()){
                    typewriter.get(MathUtils.random(0,2)).play(0.5f);
                }
                showMessageDelay = 0f; // Reset delay
                showMessageIndex++; // Increment the index

                // Check if the message display duration is over
                if (showMessageIndex > displayMessage.length() + 17) {
                    showMessage = false; // Hide the message
                    showMessageIndex = 0; // Reset index for the next message
                }
            } else {
                showMessageDelay += delta; // Increment delay
            }


        }

        player.render(batch);

        player.obj.setRegion(player.playerSheet[Gdx.input.isKeyPressed(Input.Keys.SPACE)||aim?player.rifle?3:1:0]);

        if(playerFireDelay>(player.rifle?0.2f:0.55f)&&(Gdx.input.isKeyPressed(Input.Keys.SPACE)||aim)){
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

    private boolean checkInteract() {
        if(ladderActive){
            if(ladder.coordinates.x==player.coordinates.x&&ladder.coordinates.y==player.coordinates.y){
//                print(player.coordinates+"");
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    print(player.subLevel);
                    if(player.subLevel==-1){
                        theme.stop();
                        theme=Gdx.audio.newMusic(load("lone.wav"));
                        theme.setVolume(0.4f);
                        theme.setLooping(true);
                        theme.play();
                    }
                    if(player.subLevel==-3){
                        theme.stop();
                        theme=Gdx.audio.newMusic(load("fortressy.wav"));
                        theme.setVolume(0.7f);
                        theme.setLooping(true);
                        theme.play();
                    }

                    player.subLevel--;
                    showMessage=false;
                    exploredDungeon=0;
                    generateWorld();
                    ladderSound.play();
                    return false;
                }
            }
        }

        for(Utility utility : utilities){
            if(utility.coordinates.x==player.coordinates.x&&utility.coordinates.y==player.coordinates.y){
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    if(utility.ammo) player.ammo+=MathUtils.random(2,6)*MathUtils.random(4,8);
                    else if(player.health<75)player.health+=25f;
                    else player.health=100f;
                    displayMessage=utility.res;
                    showMessageIndex=0;
                    utilities.removeValue(utility,true);
                    return true;
                }
                break;
            }
        }

        for(Vent vent : vents){
            if(vent.vent1Active(player.coordinates)||vent.vent2Active(player.coordinates)){
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    if(vent.vent1Active(player.coordinates)){
                        player.setPosition(vent.vent2_coordinates);
                    }else if(vent.vent2Active(player.coordinates)){
                        player.setPosition(vent.vent1_coordinates);
                    }
                    return false;
                }
                break;
            }
        }

        for(Item item : items){
            if(item.coordinates.x==player.coordinates.x&&item.coordinates.y==player.coordinates.y){
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    displayMessage=item.res;
                    showMessageIndex=0;
                    return true;
                }
                break;
            }
        }

        for(Memo memo : memos){
            if(memo.coordinates.x==player.coordinates.x&&memo.coordinates.y==player.coordinates.y){
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    if(memo.res==null){
                        displayMessage=docs[docsIndex];
                        memo.res=docs[docsIndex];
                        docsIndex++;
                    }else{
                        displayMessage=memo.res;
                    }
                    showMessageIndex=0;
                    return true;

                }
                break;
            }
        }

        for(NPC npc : peoples){
            if(npc.coordinates.x==player.coordinates.x&&npc.coordinates.y==player.coordinates.y){
                showInteractButton=true;
                if(Gdx.input.isKeyJustPressed(Input.Keys.Z)||shoot){
                    if(npc.canTalk){
                        if(npc.res==null){
                            displayMessage=docs[docsIndex];
                            npc.res=docs[docsIndex];
                            docsIndex++;
                        }else{
                            displayMessage=npc.res;
                        }
                        npcSound.random().play(0.7f);
                        showMessageIndex=0;
                        return true;

                    }else displayMessage="...";
                    showMessageIndex=0;
                    return true;

                }
                break;
            }
        }
        return false;
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
        if(Gdx.input.isKeyPressed(Input.Keys.Z)||shoot) {
//            print("fire");
            if(player.ammo>0){
                projectiles.add(new Projectile(player.playerSheet[player.rifle?4:2], player.coordinates, player.obj.getRotation()));
                playerFireDelay = 0f;
                fire.play(0.35f);
                player.ammo--;
            }
        }
    }

    private void controlPlayer() {

        playerSpeed= (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)||run)? (float) (0.375 / 2.5f) :0.375f;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)||moveRight){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=0){
                player.obj.setRotation(0);
//              walk.play(0.5f);
                return;
            }else if(checkCollision(player.coordinates.x+1, player.coordinates.y)) {
                player.setPosition(new Vector2(player.coordinates.x+1, player.coordinates.y));
                walk.random().play(0.35f);
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)||moveUp){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=90){
                player.obj.setRotation(90);
//                walk.play(0.5f);

                return;

            }else if(checkCollision(player.coordinates.x, player.coordinates.y+1)){
                player.setPosition(new Vector2(player.coordinates.x, player.coordinates.y+1));
                walk.random().play(0.35f);

            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)||moveLeft){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=180){
                player.obj.setRotation(180);
//                walk.play(0.5f);

                return;

            }else  if(checkCollision(player.coordinates.x-1, player.coordinates.y)) {
                player.setPosition(new Vector2(player.coordinates.x-1, player.coordinates.y));
                walk.random().play(0.35f);

            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)||moveDown){
            playerControlDelay=0f;

            if(player.obj.getRotation()!=-90){
                player.obj.setRotation(-90);
//                walk.play(0.5f);

            }else  if(checkCollision(player.coordinates.x, player.coordinates.y-1)) {
                player.setPosition(new Vector2(player.coordinates.x, player.coordinates.y-1));
                walk.random().play(0.35f);

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
        theme.pause();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        gameStartSound.play(1f);
        if(player.subLevel!=-1)theme.play();
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode==Input.Keys.X){
                    player.rifle=!player.rifle;
                }

                if(keycode==Input.Keys.ESCAPE){
                    game.setScreen(pauseScreen);
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

                for(Button btn : buttons){
                    if(btn.button.getBoundingRectangle().contains(point)){
                        print(btn.index);
                        switch(btn.index){
                            case 0:{
                                moveUp=true;
                                moveRight=false;
                                moveDown=false;
                                moveLeft=false;

                            }break;
                            case 1:{
                                moveRight=true;
                                moveDown=false;
                                moveLeft=false;
                                moveUp=false;
                            }break;
                            case 2:{
                                moveDown=true;
                                moveLeft=false;
                                moveUp=false;
                                moveRight=false;
                            }break;
                            case 3:{
                                moveLeft=true;
                                moveUp=false;
                                moveRight=false;
                                moveDown=false;
                            }break;
                            case 4:{
                                run=true;
                            }break;
                            case 5:{
                                player.rifle=!player.rifle;
                            }break;
                            case 6:{
                                shoot=true;
                            }break;
                            case 7:{
                                game.setScreen(pauseScreen);
                            }break;
                            case 8:{
                                showControls=!showControls;
                            }break;
                            case 9:{
//                                aim=!aim;
                            }break;

                            case 10:{
                                aim=!aim;
                            }break;
                        }
                    }
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point=new Vector2(touch.x,touch.y);

                for(Button btn : buttons){
                    if(btn.button.getBoundingRectangle().contains(point)){
                        print(btn.index);
                        switch(btn.index){
                            case 0:{
                                moveUp=false;
                                moveRight=false;
                                moveDown=false;
                                moveLeft=false;

                            }break;
                            case 1:{
                                moveRight=false;
                                moveDown=false;
                                moveLeft=false;
                                moveUp=false;
                            }break;
                            case 2:{
                                moveDown=false;
                                moveLeft=false;
                                moveUp=false;
                                moveRight=false;
                            }break;
                            case 3:{
                                moveLeft=false;
                                moveUp=false;
                                moveRight=false;
                                moveDown=false;
                            }break;
                            case 4:{
                                run=false;
                            }break;
                            case 5:{
                            }break;
                            case 6:{
                                shoot=false;
                            }break;
                            case 10:{
                            }break;
                        }
                    }
                }
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
                point=new Vector2(touch.x,touch.y);

//                for(Button btn : buttons){
//                    if(btn.button.getBoundingRectangle().contains(point)){
//                        print(btn.index);
//                        switch(btn.index){
//                            case 0:{
//                                moveUp=true;
//                            }break;
//                            case 1:{
//                                moveRight=true;
//                            }break;
//                            case 2:{
//                                moveDown=true;
//                            }break;
//                            case 3:{
//                                moveLeft=true;
//                            }break;
//                            case 4:{
//                                run=true;
//                            }break;
//                            case 5:{
//                                toggle=true;
//                            }break;
//                            case 6:{
//                                btn.button.setPosition(point.x-btn.button.getRegionWidth()/4f,point.y-btn.button.getRegionWidth()/4f);
//                            }break;
//                        }
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
