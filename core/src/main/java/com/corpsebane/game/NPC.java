package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.getRandomDirection;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.mercenaries;
import static com.corpsebane.game.GameScreen.pathFinder;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NPC {
    public Sprite obj;
    public int tries;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public float health,speed,moveDelay=0f;
    public boolean hasSafePath=false,canTalk=false;
    String res=null;
    public Array<Vector2> path;
    enum NPCSTATE{
        IDLE,SNEAKING
    }

    NPCSTATE state=NPCSTATE.IDLE;

    public NPC(int child,Vector2 position,boolean canTalk){
        this.sheet=extractSprites("npc_sheet.png",32,32);
        this.obj=new Sprite(sheet[child]);
//        this.health=child?MathUtils.random(1,4):MathUtils.random(5,10);
//        this.speed=child?MathUtils.random(1,6):MathUtils.random(5,12);
        this.health=MathUtils.random(5,10);
        this.canTalk=canTalk;
        this.coordinates=new Vector2(position);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);
        obj.setPosition(position.x*size.x,position.y*size.y);
        obj.setSize(size.x,size.y);
        obj.setOriginCenter();
        path=new Array<>();
        speed=0.5f;
        obj.setRotation(getRandomDirection());
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
//        print("moved to : "+position );
        setDirection(position);

        coordinates=position;
        obj.setPosition(position.x*size.x,position.y*size.y);
    }

    public void render(SpriteBatch batch,float delta){



        if(state==NPCSTATE.IDLE) {
            for (Enemy enemy : enemies) {
                if (isNearby(coordinates, enemy.coordinates, 15)) {
                    state = NPCSTATE.SNEAKING;
                    hasSafePath=false;
                    break;
                }
            }
        }
        if(state==NPCSTATE.SNEAKING&&!hasSafePath){
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
                    for(Merc merc : mercenaries){
                        if(!isNearby(merc.coordinates,randomPoint,5)){
                            hasSafePath=true;
                            path=pathFinder.findPath(coordinates,randomPoint,10);
                            break;
                        }
                    }
                    if(!isNearby(GameScreen.player.coordinates,randomPoint,5)){
                        hasSafePath=true;
                        path=pathFinder.findPath(coordinates,randomPoint,10);
                        break;
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
                    state=NPCSTATE.IDLE;
                    hasSafePath=false;
                }
            }else moveDelay+=delta;

        }
            obj.draw(batch);
    }
}
