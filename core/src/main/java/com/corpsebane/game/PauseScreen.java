package com.corpsebane.game;

import static com.corpsebane.game.CorpseBane.gameScreen;
import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.load;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class PauseScreen implements Screen {
    public SpriteBatch batch;
    public CorpseBane game;
    public Vector3 touch;
    public OrthographicCamera camera;
    Vector2 screen,point;
    ExtendViewport viewport;
    TextButton startButton,quitButton;
    boolean startButtonActive=true;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    public Sound selectSound;

    public PauseScreen(CorpseBane game){
        touch=new Vector3();
        point=new Vector2();
        this.game=game;
        this.batch=game.batch;
        setWindowed();
        font=new BitmapFont(load("font.fnt"));
        font.getData().setScale(0.5f);

        selectSound=Gdx.audio.newSound(load("select.wav"));

        shapeRenderer=new ShapeRenderer();
        startButton=new TextButton("Continue",new Rectangle(720/4f+60,120,400/1.5f,80/3f));
        quitButton=new TextButton("Quit",new Rectangle(720/4f+95,80,400/1.5f,80/3f));

    }

    private void setWindowed() {
        camera=new OrthographicCamera();
        screen=new Vector2(1280/2f,720/2f);
        viewport=new ExtendViewport(screen.x, screen.y,camera);
        camera.setToOrtho(false, screen.x, screen.y);
        viewport.apply();
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
                if(keycode== Input.Keys.W||keycode== Input.Keys.UP){
                    startButtonActive=!startButtonActive;
                }
                if(keycode== Input.Keys.S||keycode== Input.Keys.DOWN){
                    startButtonActive=!startButtonActive;
                }
                if(keycode==Input.Keys.SPACE){
                    if(startButtonActive){
                        game.setScreen(gameScreen);
                    }else{
                        Gdx.app.exit();
                    }
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point=new Vector2(touch.x,touch.y);

                if(startButton.bounds.contains(point)){
                    game.setScreen(gameScreen);
                }
                if(quitButton.bounds.contains(point)){
                    Gdx.app.exit();
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
                if(startButton.bounds.contains(point)&&!startButtonActive){
                    startButtonActive=true;
                }
                if(quitButton.bounds.contains(point)&&startButtonActive){
                    startButtonActive=false;
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point=new Vector2(touch.x,touch.y);
                if(startButton.bounds.contains(point)&&!startButtonActive){
                    startButtonActive=true;
                }
                if(quitButton.bounds.contains(point)&&startButtonActive){
                    startButtonActive=false;
                }
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(screen.x/2f,screen.y/2f,0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        startButton.render(batch,delta,startButtonActive);
        quitButton.render(batch,delta,!startButtonActive);
        batch.end();

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
}
