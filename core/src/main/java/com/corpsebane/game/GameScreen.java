package com.corpsebane.game;
import static com.corpsebane.game.Methods.load;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameScreen implements Screen {

    public CorpseBane game;
    public SpriteBatch batch;
    Vector3 touch;
    Vector2 point;
    boolean startSelected=false,endSelected=false;
    PathFinder pathFinder;
    public static OrthographicCamera camera;
    public Vector2 screen;
    public ShapeRenderer shapeRenderer;
    public Vector2 cellSize;
    public static GameCell[] gameCells;
    public Viewport viewport;

    public GameScreen(CorpseBane game){
        touch=new Vector3();

        this.game=game;
        this.batch=game.batch;

        pathFinder=new PathFinder();

        setWindowed();

        shapeRenderer=new ShapeRenderer();
        shapeRenderer.setColor(Color.WHITE);

        cellSize=new Vector2();
        cellSize.x = Gdx.graphics.getWidth() / 40f;
        cellSize.y = Gdx.graphics.getHeight() / 22f;

        initializeCells();
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

    private void initializeCells() {
        gameCells=new GameCell[880];
        int index=0;
        for (int row = 0; row < 22; row++) {
            for (int col = 0; col < 40; col++) {
                gameCells[index]=new GameCell(col,row);
                index++;
            }
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(screen.x/2f,screen.y/2f,0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(new Texture(load("icon.png")),50,50);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(GameCell cell : gameCells){
            shapeRenderer.setColor(cell.isActive?Color.CYAN:(cell.isHovered)?Color.GRAY:Color.BLACK);
            if(cell.isEnd)shapeRenderer.setColor(Color.RED);
            if(!cell.isPath)shapeRenderer.setColor(Color.LIGHT_GRAY);
            if(cell.isStart||cell.isExplored)shapeRenderer.setColor(Color.GREEN);

            shapeRenderer.rect(cell.i*cellSize.x, cell.j*cellSize.y, cellSize.x, cellSize.y);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(GameCell cell : gameCells){
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(cell.i*cellSize.x, cell.j*cellSize.y, cellSize.x, cellSize.y);
        }
        shapeRenderer.end();

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
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if(keycode==Input.Keys.SPACE && startSelected && endSelected){
                    findPath();
                }
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

                        if(!cell.isStart&&!cell.isEnd){
                            if(button==Input.Buttons.RIGHT){
                                if(cell.isPath){
                                    cell.isPath=false;
                                    print("this cell is not path");
//                                    cell.isActive=false;
                                }
                            }

                            if(button==Input.Buttons.LEFT){
                                if(!cell.isActive&&cell.isPath){
//                                    cell.isActive=true;
                                }
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

    private void findPath() {
        Vector2 startCell=new Vector2(),endCell=new Vector2();
        for(GameCell cell : gameCells){
            if(cell.isStart)startCell=new Vector2(cell.i,cell.j);
            if(cell.isEnd)endCell=new Vector2(cell.i,cell.j);
        }
        print(""+pathFinder.findPath(startCell,endCell));
    }
}
