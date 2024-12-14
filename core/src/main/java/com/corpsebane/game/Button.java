package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.getRandomDirection;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Button {
    Sprite button;
    int index;
    boolean drag=false;
    String name;
    float xOffset=0,yOffset=0;
    public Button(int index,String name){
        this.index=index;
        this.name=name;
        this.button=new Sprite();
        switch(index){
            case 0:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[0]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=-65;
                yOffset=-25;
            }break;
            case 1:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[0]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(-90);
                xOffset=-45;
                yOffset=-45;
            }break;
            case 2:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[0]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(180);
                xOffset=-65;
                yOffset=-45;
            }break;
            case 3:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[0]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(90);
                xOffset=-85;
                yOffset=-45;
            }break;
            case 4:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[1]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=85-button.getRegionWidth()/2f;
                yOffset=-43;
            }break;
            case 5:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[2]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=65-button.getRegionWidth()/2f;
                yOffset=-45;
            }break;
            case 6:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[3]);
                button.setSize(18,18);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=85-button.getRegionWidth()/2f;
                yOffset=-25;
            }break;
            case 7:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[4]);
                button.setSize(15,15);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=-83;
                yOffset=35;
            }break;
            case 8:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[5]);
                button.setSize(15,15);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=-67;
                yOffset=35;
            }break;
            case 9:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[6]);
                button.setSize(16,16);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=73;
                yOffset=35;
            }break;
            case 10:{
                this.button=new Sprite(extractSprites("controls_sheet.png",32,32)[7]);
                button.setSize(20,20);
                button.setOriginCenter();
                button.setRotation(0);
                xOffset=-40;
                yOffset=-25;
            }break;

        }

    }
    public void setPosition(Vector2 player){
        button.setPosition(player.x+xOffset,player.y+yOffset);

    }
    public void render(SpriteBatch batch,Vector2 player){
        setPosition(player);
        button.draw(batch);
    }
}
