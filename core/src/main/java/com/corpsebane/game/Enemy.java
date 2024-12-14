package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.enemies;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.isNearby;
import static com.corpsebane.game.GameScreen.monsterGrowl;
import static com.corpsebane.game.GameScreen.monsterWalk;
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
    public float damage=0,health=10,armor=10,speed=0;
    public boolean hasChasePath=false;
    float moveDelay=0.5f;
    private int patrolIndex = 0;
    public Array<Vector2> path,patrolPath;
    float stageDelay=0f;
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
        this.type=type;
        transform(type);
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
        monsterWalk.random().play(0.45f);
        setDirection(position);
        coordinates=position;
        obj.setPosition(position.x*size.x,position.y*size.y);
    }

    public void transform(int type){
        switch (type){
            case 0:{
                damage=2f;
                health= MathUtils.random(2,6);
                armor=MathUtils.random(0,2);
                speed=0.3f;
            }break;
            case 1:{
                damage=5f;
                health= MathUtils.random(5,8);
                armor=MathUtils.random(2,4);
                speed=0.6f;
            }break;
            case 2:{
                damage=7f;
                health= MathUtils.random(6,12);
                armor=MathUtils.random(3,7);
                speed=0.4f;

            }break;
            case 3:{
                damage=8f;
                health= MathUtils.random(3,6);
                armor=MathUtils.random(10,15);
                speed=0.35f;
            }break;
            case 4:{
                damage=22f;
                health= MathUtils.random(3,6);
                armor=MathUtils.random(10,15);
                speed=0.15f;
            }break;
            case 5:{
                damage=4f;
                health= MathUtils.random(5,8);
                armor=MathUtils.random(10,16);
                speed=0.5f;
            }break;
        }
    }

    public void render(SpriteBatch batch,float delta){



        if(type<3){
            if(stageDelay>(120*2.5f)){
                transform(type++);
                stageDelay=0;
            }
            stageDelay+=delta;
        }

//        print(""+state);
//        coordinates=new Vector2(obj.getX()/size.x,obj.getY()/size.y);
        if(state== MOBSTATE.IDLE||state==MOBSTATE.PATROLLING) {
//            print("is idle");
            if(MathUtils.randomBoolean(0.2f)&&type>1){
                monsterGrowl.random().play(0.45f);
            }
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
                randomCoordinates=getRandomCellPath();
                if(!isNearby(coordinates,randomCoordinates,MathUtils.random(6,12))){
                    patrolPath=new Array<>();
                    patrolPath=pathFinder.findPath(coordinates,randomCoordinates,MathUtils.random(6,12));
                    patrolPath.reverse();
                    patrolPath.pop();
                    Array<Vector2> reversePath = new Array<>(patrolPath);
                    reversePath.pop();
                    reversePath.reverse();
                    patrolPath.addAll(reversePath);
                    patrolIndex = 0;
                    state = MOBSTATE.PATROLLING;
                }
            }
        }

        if(state==MOBSTATE.PATROLLING){
            if(moveDelay>speed){
                if(patrolIndex>patrolPath.size-1){
                    patrolIndex=0;
                }
//                print(patrolIndex+" , "+coordinates);
                setPosition(patrolPath.get(patrolIndex));
                moveDelay=0f;
                patrolIndex++;
            }else moveDelay+=delta;
        }



        if(state==MOBSTATE.CHASING&&path.size>1){
            if(moveDelay>speed){
                if(type==3
                    && MathUtils.random(0, 30) % 5 == 0
                    && MathUtils.randomBoolean(0.1f)
                    && (coordinates.x + coordinates.y) % 7 == 3
                ){
                    enemies.add(new Enemy(MathUtils.random(0,1),coordinates,obj.getRotation()));
                    print("enemy spawned");
                }
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
