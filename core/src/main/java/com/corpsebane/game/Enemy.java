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

public class Enemy {
    public Sprite obj;
    public int type;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public float damage=0,health=0,armor=0,speed=0;

    public Enemy(int type,Vector2 position,float direction){
        this.sheet=extractSprites("mob_sheet.png",32,32);
        this.obj=new Sprite(sheet[type]);
        this.coordinates=new Vector2(position);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        switch (type){
            case 0:{
                damage=MathUtils.random(1,2);
                health= MathUtils.random(2,3);
                armor=MathUtils.random(1,2);
                speed=MathUtils.random(1,2);
            }break;
            case 1:{
                damage=MathUtils.random(3,6);
                health= MathUtils.random(3,4);
                armor=MathUtils.random(3,7);
                speed=MathUtils.random(2,4);
            }break;
            case 2:{
                damage=MathUtils.random(5,10);
                health= MathUtils.random(12,20);
                armor=MathUtils.random(2,4);
                speed=MathUtils.random(6,10);

            }break;
            case 3:{
                damage=MathUtils.random(1,2);
                health= MathUtils.random(12,20);
                armor=MathUtils.random(1,2);
                speed=MathUtils.random(1,2);
            }break;
        }
    }

    public void render(SpriteBatch batch){
        obj.draw(batch);
    }
}
