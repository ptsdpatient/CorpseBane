package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.getRandomDirection;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Utility {
    int index;
    Vector2 coordinates,size;
    String res;
    Sprite obj;
    public Utility(boolean ammo, Vector2 position){
        this.res= ammo?"It's a box of ammunation":"It's a med kit";
        this.obj=new Sprite(extractSprites("utility_sheet.png",32,32)[index]);
        coordinates=new Vector2(position);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        obj.setRotation(getRandomDirection());
        obj.setScale(MathUtils.random(0.7f,1));
    }

    public void render(SpriteBatch batch){
        obj.draw(batch);
    }
}
