package com.corpsebane.game;
import static com.corpsebane.game.Methods.load;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.Gdx;
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
    public static OrthographicCamera camera;
    public Vector2 screen;
    public ShapeRenderer shapeRenderer;
    public Vector2 cellSize;
    public Viewport viewport;

    public GameScreen(CorpseBane game){
        this.game=game;
        this.batch=game.batch;
        camera=new OrthographicCamera();
        screen=new Vector2(1280/2f,720/2f);
        viewport=new ExtendViewport(screen.x, screen.y,camera);

        camera.setToOrtho(false, screen.x, screen.y);
        viewport.apply();

        shapeRenderer=new ShapeRenderer();
        shapeRenderer.setColor(Color.WHITE);
        cellSize=new Vector2();
        cellSize.x = Gdx.graphics.getWidth() / 40f;
        cellSize.y = Gdx.graphics.getHeight() / 22f;

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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int row = 0; row < 22; row++) {
            for (int col = 0; col < 40; col++) {
                float x = col * cellSize.x;
                float y = row * cellSize.y;
                shapeRenderer.rect(x, y, cellSize.x, cellSize.y);
            }
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

    }
}
