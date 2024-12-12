package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.dungeons;
import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.GameScreen.getCellIndex;
import static com.corpsebane.game.GameScreen.getRandomCellInRectangle;
import static com.corpsebane.game.GameScreen.getRandomCellPath;
import static com.corpsebane.game.GameScreen.getRandomDirection;
import static com.corpsebane.game.GameScreen.screen;
import static com.corpsebane.game.Methods.extractSprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Vent {
    Sprite vent1,vent2;
    public TextureRegion[] sheet;
    public Vector2 size,vent1_coordinates,vent2_coordinates;
    public Vent(){
        this.sheet=extractSprites("door_sheet.png",32,32);
        size=new Vector2(screen.x/COLS,screen.y/ROWS);

        this.vent1=new Sprite(sheet[0]);
        vent1_coordinates=new Vector2(getRandomCellInRectangle(dungeons.random().dungeon));
        vent1.setPosition(vent1_coordinates.x*size.x,vent1_coordinates.y*size.y);
        vent1.setSize(size.x,size.y);
        vent1.setOriginCenter();
        vent1.setRotation(getRandomDirection());
        vent1.setScale(MathUtils.random(0.6f,1.1f));

        this.vent2=new Sprite(sheet[0]);
        vent2_coordinates=new Vector2(getRandomCellPath());
        vent2.setPosition(vent2_coordinates.x*size.x,vent2_coordinates.y*size.y);
        vent2.setSize(size.x,size.y);
        vent2.setOriginCenter();
        vent2.setRotation(getRandomDirection());


    }

    public boolean vent1Active(Vector2 playerCoordinates){
        return (playerCoordinates.x==vent1_coordinates.x&&playerCoordinates.y==vent1_coordinates.y);
    }
    public boolean vent2Active(Vector2 playerCoordinates){
        return (playerCoordinates.x==vent2_coordinates.x&&playerCoordinates.y==vent2_coordinates.y);
    }

    public void render(SpriteBatch batch){
//        if(gameCells[getCellIndex((int) vent1_coordinates.y, (int) vent1_coordinates.x)].isPath){
            vent1.draw(batch);
//        }

//        if(gameCells[getCellIndex((int) vent2_coordinates.y, (int) vent2_coordinates.x)].isPath){
            vent2.draw(batch);
//        }

    }
}
