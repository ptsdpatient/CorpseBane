package com.corpsebane.game;

import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.load;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MenuScreen implements Screen {
    private final SpriteBatch batch;
    public CorpseBane game;
    public Vector3 touch;
    public OrthographicCamera camera;
    Vector2 screen,point;
    ExtendViewport viewport;
    Texture bg,title;
    TextButton startButton,quitButton;
    boolean startButtonActive=true;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    public Sound selectSound;
    public Music theme;

    public MenuScreen(CorpseBane game){
        touch=new Vector3();
        point=new Vector2();
        this.game=game;
        this.batch=game.batch;
        setWindowed();
        font=new BitmapFont(load("font.fnt"));
        font.getData().setScale(0.5f);
        font.setColor(Color.CHARTREUSE);

        selectSound=Gdx.audio.newSound(load("select.wav"));
        theme=Gdx.audio.newMusic(load("lone.wav"));
        theme.setLooping(true);
        theme.setVolume(0.8f);

        bg=new Texture(load("bg.jpeg"));
        title=new Texture(load("title.png"));
        shapeRenderer=new ShapeRenderer();
        startButton=new TextButton("Play",new Rectangle(50,100,400/1.5f,80/3f));
        quitButton=new TextButton("Quit",new Rectangle(50,60,400/1.5f,80/3f));

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
        theme.play();
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if(keycode== Input.Keys.W||keycode== Input.Keys.UP){
                    startButtonActive=!startButtonActive;
                    selectSound.play(0.7f);

                }
                if(keycode== Input.Keys.S||keycode== Input.Keys.DOWN){
                    startButtonActive=!startButtonActive;
                    selectSound.play(0.7f);

                }
                if(keycode==Input.Keys.SPACE){
                    if(startButtonActive){
                        game.setGameScreen();
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
                    game.setGameScreen();
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
                    selectSound.play(0.7f);

                }
                if(quitButton.bounds.contains(point)&&startButtonActive){
                    startButtonActive=false;
                    selectSound.play(0.7f);

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
                    selectSound.play(0.7f);

                }
                if(quitButton.bounds.contains(point)&&startButtonActive){
                    startButtonActive=false;
                    selectSound.play(0.7f);

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
        batch.draw(bg,0,0, screen.x,screen.y);
        batch.draw(title,20,240,300,100);
        startButton.render(batch,delta,startButtonActive);
        quitButton.render(batch,delta,!startButtonActive);
        font.draw(batch,"Sound and Music done by : blackborzoi",300,200,300, Align.center,true);
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
        theme.pause();
    }

    @Override
    public void dispose() {

    }
}
