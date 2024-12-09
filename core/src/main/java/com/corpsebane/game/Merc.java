package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.pathFinder;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Merc {
    public Sprite obj;
    public int type,tries;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public float health,speed,moveDelay=0f,fireRate=0.35f;
    public boolean hasSafePath=false;
    public Array<Vector2> path;
    enum MercState{
        IDLE,SNEAKING,AIM,SHOOT
    }

    MercState state= MercState.IDLE;

    public Merc(boolean bad,Vector2 position,float direction){
        this.sheet=extractSprites("merc_sheet.png",32,32);
        this.obj=new Sprite(sheet[bad?0:2]);
        this.health=bad? MathUtils.random(1,4):MathUtils.random(5,10);
        this.speed=bad?MathUtils.random(1,6):MathUtils.random(5,12);
        this.coordinates=new Vector2(position);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        path=new Array<>();
        speed=0.5f;
    }

    public void setPosition(Vector2 position){
//        print("moved to : "+position );
        coordinates=position;
        obj.setPosition(position.x*size.x,position.y*size.y);
    }

    public void render(SpriteBatch batch, float delta){
        if(state== MercState.IDLE) {
            for (Enemy enemy : enemies) {
                if (isNearby(coordinates, enemy.coordinates, 4)) {
                    state = MercState.SNEAKING;
                    hasSafePath=false;
                    break;
                }
                if(isNearby(coordinates, enemy.coordinates, 14)){
                    state=MercState.AIM;
                    tries=0;
                    Vector2 randomCoordinate=getRandomCellPath();
//                    while(tries<30||randomCoordinate.x!=enemy.coordinates.x||randomCoordinate.y!=enemy.coordinates.y){
//
//                    }
                    break;
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
                    hasSafePath=false;
                }
            }else moveDelay+=delta;

        }
        obj.draw(batch);
    }
}
