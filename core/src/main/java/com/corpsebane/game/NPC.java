package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class NPC {
    public Sprite obj;
    public int type;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public float health=0,speed=0;

    public NPC(boolean child,Vector2 position,float direction){
        this.sheet=extractSprites("npc_sheet.png",32,32);
        this.obj=new Sprite(sheet[child?1:0]);
        this.health=child?MathUtils.random(1,4):MathUtils.random(5,10);
        this.speed=child?MathUtils.random(1,6):MathUtils.random(5,12);
        this.coordinates=new Vector2(position);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
    }


    public void render(SpriteBatch batch){
        if(gameCells[getCellIndex((int) coordinates.y, (int) coordinates.x)].isPath){
            obj.draw(batch);
        }
    }
}
