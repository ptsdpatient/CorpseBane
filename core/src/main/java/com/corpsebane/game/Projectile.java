package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.checkCollision;
import static com.corpsebane.game.GameScreen.screen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    Vector2 direction,projectileSize,coordinates;
    Sprite obj;
    boolean isDead=false;
    float speed=0.5f,speedDelay=0f;
    public Projectile(TextureRegion texture, Vector2 position, float direction){
        obj=new Sprite(texture);
        projectileSize=new Vector2(screen.x/COLS,screen.y/ROWS);
        coordinates=position;
        obj.setRotation(direction);
        obj.setPosition(coordinates.x*projectileSize.x,coordinates.y*projectileSize.y);
        obj.setSize(projectileSize.x,projectileSize.y);
        obj.setOriginCenter();
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
        if(speedDelay>speed){
            speedDelay=0f;
            if(checkCollision(coordinates.x,coordinates.y)){
                coordinates.x+=direction.x;
                coordinates.y+=direction.y;
                obj.setPosition(coordinates.x*projectileSize.x,coordinates.y*projectileSize.y);
            }else{
                isDead=true;
            }
        }else{
            speedDelay+=delta;
        }
    }
}
