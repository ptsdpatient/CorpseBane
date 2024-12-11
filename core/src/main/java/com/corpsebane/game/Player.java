package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Sprite obj;
    public int type,subLevel=1;
    public boolean rifle=false;
    public float health=100,ammo=1040f;
    public TextureRegion[] playerSheet;
    public Vector2 playerSize,coordinates;
    public Player(String name){
        this.playerSheet=extractSprites("player_sheet.png",32,32);
        this.obj=new Sprite(playerSheet[0]);
        switch(name){
            case "player":type=0;
                this.obj=new Sprite(playerSheet[0]);
            break;
            case "bullet":type=1;
                this.obj=new Sprite(playerSheet[3]);
            break;
            case "drone":type=2;
                this.obj=new Sprite(playerSheet[4]);
            break;
        }
        playerSize=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setSize(playerSize.x,playerSize.y);
        obj.setOriginCenter();
    }
    public void setPosition(Vector2 position){
        coordinates=position;
        obj.setPosition(position.x*playerSize.x,position.y*playerSize.y);
    }

    public void render(SpriteBatch batch){
        obj.draw(batch);
    }
}
