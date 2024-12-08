package com.corpsebane.game;


import static com.corpsebane.game.GameScreen.COLS;
import static com.corpsebane.game.GameScreen.ROWS;
import static com.corpsebane.game.GameScreen.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameCell {
    int id=0;
    int i,j;
    boolean isRoad=false,isBorder=false,isActive=false,isHovered=false,isStart=false,isEnd=false,isPath=false,isExplored=false;
    Vector2 cellSize;
    float hcost=0,fcost=0,gcost=0;
    public GameCell(int i,int j,int id){
        this.i=i;
        this.id=id;
        this.j=j;
        this.cellSize = new Vector2(screen.x/COLS,screen.y/ROWS);
    }
    public GameCell(int i,int j){
        this.i=i;
        this.j=j;
        this.cellSize = new Vector2(screen.x/COLS,screen.y/ROWS);
    }

    public boolean isTouching(Vector2 point){
        return new Rectangle(i*cellSize.x, j*cellSize.y, cellSize.x, cellSize.y).contains(point);
    }
    public Rectangle getCell(){
        return new Rectangle(i*cellSize.x, j*cellSize.y, cellSize.x, cellSize.y);
    }

}
