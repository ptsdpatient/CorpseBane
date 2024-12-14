package com.corpsebane.game;

import static com.corpsebane.game.Methods.load;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CorpseBane extends Game {
    public SpriteBatch batch;
    public static GameScreen gameScreen;
    public static MenuScreen menuScreen;
    public static DiedScreen diedScreen;
    public static PauseScreen pauseScreen;

    @Override
    public void create() {
        Gdx.graphics.setCursor( Gdx.graphics.newCursor(new Pixmap(load("cursor.png")), 0, 0));
        batch=new SpriteBatch();
        gameScreen=new GameScreen(this);
        menuScreen=new MenuScreen(this);
        diedScreen= new DiedScreen(this);
        pauseScreen=new PauseScreen(this);
//        setScreen(menuScreen);
    }
    public void setDiedScreen(){
        setScreen(diedScreen);
    }
    public void setGameScreen(){
        gameScreen.generateWorld();
        setScreen(gameScreen);
    }
}
