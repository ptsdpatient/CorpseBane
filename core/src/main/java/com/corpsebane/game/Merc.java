package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.isPlayerBad;
import static com.corpsebane.game.GameScreen.pathFinder;
import static com.corpsebane.game.GameScreen.player;
import static com.corpsebane.game.GameScreen.projectiles;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Merc {
    public Sprite obj;
    public int tries;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates,randomCoordinate;
    public float health,speed,moveDelay=0f,fireRate=0.35f,shootDelay=0f;
    public boolean hasSafePath=false,bad;
    public Array<Vector2> path,check;

    enum MercState{
        IDLE,SNEAKING,AIM
    }

    MercState state= MercState.IDLE;

    public Merc(boolean bad,Vector2 position,float direction){
        this.sheet=extractSprites("merc_sheet.png",32,32);
        this.obj=new Sprite(sheet[bad?0:2]);
        this.health=MathUtils.random(5,10);
        this.coordinates=new Vector2(position);
        this.check=new Array<>();
        randomCoordinate=new Vector2();
        this.bad=bad;
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        path=new Array<>();
        speed=0.5f;
    }

    public void setDirection(Vector2 targetPosition){
        Vector2 difference = targetPosition.cpy().sub(coordinates);

        if (Math.abs(difference.x) > Math.abs(difference.y)) {
            if (difference.x > 0) {
                obj.setRotation(0);
            } else {
                obj.setRotation(180);
            }
        } else {
            if (difference.y > 0) {
                obj.setRotation(90);
            } else {
                obj.setRotation(-90);
            }
        }
    }

    public void setPosition(Vector2 position){
        setDirection(position);
        coordinates=position;
        obj.setPosition(position.x*size.x,position.y*size.y);
    }

    public void render(SpriteBatch batch, float delta){
        if(state== MercState.IDLE) {
            for (Enemy enemy : enemies) {

                if (isNearby(coordinates, enemy.coordinates, 1)) {
                    state = MercState.SNEAKING;
                    check=new Array<>();
                    hasSafePath=false;
                    break;
                }

                if((isNearby(coordinates, enemy.coordinates, 5) &&(coordinates.x==enemy.coordinates.x||coordinates.y==enemy.coordinates.y))||(isNearby(coordinates, player.coordinates, 5)&&isPlayerBad() &&(coordinates.x==player.coordinates.x||coordinates.y==player.coordinates.y))){
                    if(check.size<=2)check=pathFinder.findPath(coordinates, (isNearby(coordinates,player.coordinates,6)&&isPlayerBad())?player.coordinates:enemy.coordinates, 6);
                    if(shootDelay>fireRate&&check.size>=2){
                        shootDelay=0f;
                        setDirection(enemy.coordinates);
                        projectiles.add(new Projectile(sheet[bad?1:3],coordinates,obj.getRotation()));
                    }else shootDelay+=delta;
                }else if(isNearby(coordinates, enemy.coordinates,16)&&!isNearby(coordinates, enemy.coordinates, 9)){
                    tries=0;
                    while(tries<20){
                        tries++;
                        randomCoordinate=getRandomCellPath();
                        if(isNearby(coordinates,randomCoordinate,4)&&((randomCoordinate.x==enemy.coordinates.x||randomCoordinate.y==enemy.coordinates.y))){
                            path=pathFinder.findPath(coordinates,randomCoordinate,4);
                            state=MercState.AIM;
                            print("aiming");
                            break;
                        }
                    }
                }
            }
        }

        if(state== MercState.SNEAKING&&!hasSafePath){
            tries=0;
            while(!hasSafePath || tries<30){
                tries++;
                Vector2 randomPoint= getRandomCellPath();
                if(isNearby(coordinates,randomPoint,6)){
                    for(Enemy enemy : enemies){
                        if(!isNearby(enemy.coordinates,randomPoint,5)){
                            hasSafePath=true;
                            path=pathFinder.findPath(coordinates,randomPoint,10);
                            break;
                        }
                    }
                }
            }
        } else if (path.size>1) {
            if(moveDelay>speed){
                for(Enemy enemy : enemies){
                    if (isNearby(coordinates, enemy.coordinates, 1)) {
                        health-=1f;
                        break;
                    }
                }
                setPosition(path.peek());
                path.pop();
                moveDelay=0f;
                if(path.size<=1){
                    state= MercState.IDLE;
                    check=new Array<>();
                    hasSafePath=false;
                }
            }else moveDelay+=delta;

        }
        obj.draw(batch);
    }
}
