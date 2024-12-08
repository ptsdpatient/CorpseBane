package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.checkCollision;
import static com.corpsebane.game.GameScreen.enemies;
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

    Vector2 direction,projectileSize,coordinates;
    Sprite obj;
    boolean isDead=false;
    float speed=0.075f,speedDelay=0f;
    public Projectile(TextureRegion texture, Vector2 position, float direction){
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
        obj.draw(batch);


        for(NPC npc : peoples){
            if (npc.coordinates.x == coordinates.x &&npc.coordinates.y == coordinates.y ) {
                isDead = true;
                npc.health-= MathUtils.random(0,3);
                break;
            }
        }
        for(Enemy enemy : enemies){
            if (enemy.coordinates.x == coordinates.x &&enemy.coordinates.y == coordinates.y ) {
                isDead = true;
                if(enemy.armor>0){
                    enemy.armor-= MathUtils.random(0,3);
                }else{
                    enemy.health-= MathUtils.random(0,3);
                }
                break;
            }
        }
        if(speedDelay>speed){
            speedDelay=0f;

            if(checkCollision(coordinates.x,coordinates.y)){
                this.coordinates.x+=direction.x;
                this.coordinates.y+=direction.y;
                print("projectile : "+this.coordinates+"player : "+player.coordinates);
                obj.setPosition(this.coordinates.x*projectileSize.x,this.coordinates.y*projectileSize.y);
            }else{
                isDead=true;
            }
        }else{
            speedDelay+=delta;
        }
    }
}
