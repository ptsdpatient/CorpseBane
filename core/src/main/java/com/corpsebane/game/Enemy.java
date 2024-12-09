package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.pathFinder;
import static com.corpsebane.game.GameScreen.peoples;
import static com.corpsebane.game.GameScreen.player;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;
import static com.corpsebane.game.Methods.load;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    public Sprite obj;
    public int type;
    public TextureRegion[] sheet;
    public Vector2 size,coordinates;
    public float damage=0,health=0,armor=0,speed=0;
    public boolean hasChasePath=false;
    float moveDelay=0.5f;
    private int patrolIndex = 0;
    private boolean forward = true;
    public Array<Vector2> path,patrolPath;
    int tries=0;
    Vector2 randomCoordinates,target;

    enum MOBSTATE{
        IDLE,PATROLLING,CHASING
    }

    MOBSTATE state = MOBSTATE.IDLE;
    public Enemy(int type,Vector2 position,float direction){
        randomCoordinates=new Vector2();
        target=new Vector2();
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
        speed=0.45f;
    }

    public void setPosition(Vector2 position){
        coordinates=position;
        obj.setPosition(position.x*size.x,position.y*size.y);
    }

    public void render(SpriteBatch batch,float delta){

//        print(""+state);
//        coordinates=new Vector2(obj.getX()/size.x,obj.getY()/size.y);
        if(state== MOBSTATE.IDLE||state==MOBSTATE.PATROLLING) {
//            print("is idle");
            for (NPC npc : peoples) {
                if (isNearby(coordinates, npc.coordinates, 5)) {
                    state = MOBSTATE.CHASING;
                    path=pathFinder.findPath(coordinates,npc.coordinates,10);
                    hasChasePath=true;
                }
            }
            if(isNearby(coordinates, player.coordinates,5)){
                state = MOBSTATE.CHASING;
                path=pathFinder.findPath(coordinates,player.coordinates,10);
                hasChasePath=true;
            }
            if(state == MOBSTATE.IDLE && !hasChasePath){
                print("patroling");
                randomCoordinates=getRandomCellPath();
                if(!isNearby(coordinates,randomCoordinates,MathUtils.random(6,12))){
                    patrolPath=new Array<>();
                    patrolPath=pathFinder.findPath(coordinates,randomCoordinates,MathUtils.random(6,12));
                    patrolPath.reverse();
                    patrolPath.pop();
                    print("old patrol path : "+coordinates+" \n"+patrolPath+" ");
                    Array<Vector2> reversePath = new Array<>(patrolPath);
                    reversePath.pop();
                    reversePath.reverse();
                    patrolPath.addAll(reversePath);
                    patrolIndex = 0;
                    print("new patrol path : " + patrolPath);
                    state = MOBSTATE.PATROLLING;
                }
            }
        }

        if(state==MOBSTATE.PATROLLING){
            if(moveDelay>speed){
                if(patrolIndex>patrolPath.size-1){
                    patrolIndex=0;
                }
                print(patrolIndex+" , "+coordinates);
                setPosition(patrolPath.get(patrolIndex));
                moveDelay=0f;
                patrolIndex++;
            }else moveDelay+=delta;
        }



        if(state==MOBSTATE.CHASING&&path.size>1){
//            print("it should be chasing! ");
            if(moveDelay>speed){
                setPosition(path.peek());
                path.pop();
                moveDelay=0f;
                if(path.size<=1){
                    for (NPC npc : peoples) {
                        if (isNearby(coordinates, npc.coordinates, 8)) {
                            state = MOBSTATE.CHASING;
                            path=pathFinder.findPath(coordinates,npc.coordinates,10);
                            hasChasePath=true;
                        }
                    }
                    if(isNearby(coordinates, player.coordinates,8)){
                        state = MOBSTATE.CHASING;
                        path=pathFinder.findPath(coordinates,player.coordinates,10);
                        hasChasePath=true;
                    }
                    if(state == MOBSTATE.IDLE && !hasChasePath){
                        randomCoordinates=getRandomCellPath();
                        if(!isNearby(coordinates,randomCoordinates,MathUtils.random(5,16))){
                            patrolPath=pathFinder.findPath(coordinates,randomCoordinates,MathUtils.random(5,16));
                            Array<Vector2> reversePath=patrolPath;
                            reversePath.reverse();
                            patrolPath.addAll(reversePath);
                            state=MOBSTATE.PATROLLING;
                        }
                    }
                }
            }else moveDelay+=delta;
        }

        if(state==MOBSTATE.CHASING&&path.size<=1){
            state= MOBSTATE.IDLE;
            hasChasePath=false;
        }



//        if(gameCells[getCellIndex((int) coordinates.y, (int) coordinates.x)].isPath)
            obj.draw(batch);

    }


}
