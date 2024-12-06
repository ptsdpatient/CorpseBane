package com.corpsebane.game;

import static com.corpsebane.game.Methods.load;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CorpseBane extends Game {
    public SpriteBatch batch;
    public StartScreen startScreen;
    public GameScreen gameScreen;

    @Override
    public void create() {
        Gdx.graphics.setCursor( Gdx.graphics.newCursor(new Pixmap(load("cursor.png")), 0, 0));
        batch=new SpriteBatch();
        startScreen=new StartScreen(this);
        gameScreen=new GameScreen(this);
        setScreen(gameScreen);
    }
}
