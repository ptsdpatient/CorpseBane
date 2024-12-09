package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Puddle {
    public Sprite obj;
    public int index;
    public TextureRegion[] sheet;
    boolean isDead=false;
    public Vector2 size,coordinates;
    public Puddle(int index, Vector2 position){
        this.sheet=extractSprites("puddle_sheet.png",32,32);
        this.obj=new Sprite(sheet[index]);;
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        coordinates=new Vector2(position);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
    }
    public void render(SpriteBatch batch){

        obj.draw(batch);
    }
}
