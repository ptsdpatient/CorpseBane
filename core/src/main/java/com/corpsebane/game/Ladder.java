package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.getRandomDirection;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ladder {
    public Sprite obj;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public Ladder(Vector2 position){
        this.sheet=extractSprites("door_sheet.png",32,32);
        this.obj=new Sprite(sheet[1]);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        coordinates=new Vector2(position);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        obj.setScale(MathUtils.random(0.6f,1));
    }
    public void render(SpriteBatch batch){
        obj.draw(batch);
    }
}
