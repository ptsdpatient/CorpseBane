package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.checkCollision;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.hurt;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.mercenaries;
import static com.corpsebane.game.GameScreen.npcKills;
import static com.corpsebane.game.GameScreen.peoples;
import static com.corpsebane.game.GameScreen.player;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    Vector2 direction=new Vector2(1,0),projectileSize,coordinates,startPosition;
    Sprite obj;
    boolean isDead=false;
    float speed=0.075f,speedDelay=0f;
    public Projectile(TextureRegion texture, Vector2 position, float direction){
        this.startPosition=new Vector2(position);
        this.obj=new Sprite(texture);
        this.projectileSize=new Vector2(screen.x/COLS,screen.y/ROWS);
        this.coordinates = new Vector2(position);

        this.obj.setRotation(direction);
        this.obj.setPosition(coordinates.x*projectileSize.x,coordinates.y*projectileSize.y);
        this.obj.setSize(projectileSize.x,projectileSize.y);
        this.obj.setOriginCenter();
        switch((int) direction){
            case 0:{
                this.direction=new Vector2(1,0);
            }break;
            case 180:{
                this.direction=new Vector2(-1,0);
            }break;
            case -90:{
                this.direction=new Vector2(0,-1);
            }break;
            case 90:{
                this.direction=new Vector2(0,1);
            }break;
        }
    }
    public void render(SpriteBatch batch, float delta){
        if(gameCells[getCellIndex((int) coordinates.y, (int) coordinates.x)].isPath){
            obj.draw(batch);
        }

        if(!isNearby(coordinates,startPosition,1)) {
            for(NPC npc : peoples){
                if (npc.coordinates.x == coordinates.x &&npc.coordinates.y == coordinates.y ) {
                    isDead = true;
                    hurt.play(0.5f);
                    npc.health-= MathUtils.random(0,3);
                    if(npc.state== NPC.NPCSTATE.IDLE){
                        npc.hasSafePath=false;
                        npc.state=NPC.NPCSTATE.SNEAKING;
                        int randomRes=MathUtils.random(0,6);
                        switch (randomRes){
                            case 0:{
                                npc.res="What's wrong with you";
                            }break;
                            case 1:{
                                npc.res="We’re all stuck here, and you’re making it worse!";
                            }break;
                            case 2:{
                                npc.res="Leave me alone. We have bigger problems.";
                            }break;
                            case 3:{
                                npc.res="You think you’re a hero? You’re just a monster!";
                            }break;
                            case 4:{
                                npc.res="Have you lost your mind!";
                            }break;
                            case 5:{
                                npc.res="...";
                            }break;
                            case 6:{
                                npc.res="No, please! I’m not your enemy!";
                            }break;
                        }
                    }
                    break;
                }
            }

            for(Merc merc : mercenaries){
                if (merc.coordinates.x == coordinates.x &&merc.coordinates.y == coordinates.y ) {
                    isDead = true;
                    hurt.play(0.5f);
                    merc.health-= MathUtils.random(0,3);
                    hurt.play(0.5f);
                    if(merc.state== Merc.MercState.IDLE){
                        merc.hasSafePath=false;
                        merc.state=Merc.MercState.AIM;
                        npcKills+=7;
                    }
                    break;
                }
            }
            if (player.coordinates.x == coordinates.x &&player.coordinates.y == coordinates.y ) {
                isDead = true;
                hurt.play(0.5f);
                player.health -= MathUtils.random(0,15);
            }

            for(Enemy enemy : enemies){
                if (enemy.coordinates.x == coordinates.x &&enemy.coordinates.y == coordinates.y ) {
                    isDead = true;
                    hurt.play(0.5f);
                    if(enemy.armor>0){
                        enemy.armor-= MathUtils.random(0,4);
                    }else{
                        enemy.health-= MathUtils.random(0,4);
                    }
                    break;
                }
            }
        }
        if(speedDelay>speed){
            speedDelay=0f;

            if(checkCollision(coordinates.x,coordinates.y)){
                this.coordinates.x+=direction.x;
                this.coordinates.y+=direction.y;
//                print("projectile : "+this.coordinates+"player : "+player.coordinates);
                obj.setPosition(this.coordinates.x*projectileSize.x,this.coordinates.y*projectileSize.y);
            }else{
                isDead=true;
            }
        }else{
            speedDelay+=delta;
        }
    }
}
