package com.corpsebane.game;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameCell {
    int i,j;
    boolean isActive=false,isHovered=false,isStart=false,isEnd=false,isPath=true,isExplored=false;
    Vector2 cellSize;
    float hcost=0,fcost=0,gcost=0;
    public GameCell(int i,int j){
        this.i=i;
        this.j=j;
        this.cellSize = new Vector2(16,16.363636f);
    }

    public boolean isTouching(Vector2 point){
        return new Rectangle(i*cellSize.x, j*cellSize.y, cellSize.x, cellSize.y).contains(point);
    }
    public Rectangle getCell(){
        return new Rectangle(i*cellSize.x, j*cellSize.y, cellSize.x, cellSize.y);
    }

}
