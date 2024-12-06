package com.corpsebane.game;

import static com.corpsebane.game.GameScreen.gameCells;
import static com.corpsebane.game.Methods.print;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathFinder {
    public int[][] neighbouringCellsTraversingSequence;
    public Array<Vector2> selectedPath;
    public Array<GameCell> calculateCell,exploredPath;
    public Vector2 currentCell;

    public boolean targetFound;
    public PathFinder(){
        selectedPath=new Array<>();
        exploredPath=new Array<>();
        calculateCell=new Array<>();
        currentCell=new Vector2();
        this.neighbouringCellsTraversingSequence = new int[][]{
            {-1, 1}, {0, 1}, {1, 1},
            {-1, 0}, {0, 0}, {1, 0},
            {-1, -1}, {0, -1}, {1, -1}
        };
        targetFound=false;
    }
    public Array<Vector2> findPath(Vector2 startPosition,Vector2 endPosition){
        initializeCell();
        selectedPath.add(startPosition);
        exploredPath.add(new GameCell((int) startPosition.x, (int) startPosition.y));
        exploredPath.peek().fcost=3000;
        currentCell=startPosition;
        int index=0;
        while(!targetFound){
            index++;

            for(GameCell cell : gameCells){
                if (Math.abs(cell.i - currentCell.x) <= 1 && Math.abs(cell.j - currentCell.y) <= 1) {
                    if (!(cell.i == currentCell.x && cell.j == currentCell.y)) {
                        if(cell.isPath)calculateCell.add(cell);
                    }
                }
            }

            calculateCost(endPosition);

            if((currentCell.x==endPosition.x && currentCell.y==endPosition.y)||index>10)
            {
                targetFound=true;
                for(GameCell cell : exploredPath){
                    if(cell.isExplored&&cell.isPath){
                        selectedPath.add(new Vector2(cell.i,cell.j));
                    }
                }
            }
        }
        return selectedPath;
    }

    private void calculateCost(Vector2 endPosition) {

        for(GameCell cell : calculateCell){
            cell.gcost = (float) Math.sqrt(Math.pow(cell.i - currentCell.x, 2) + Math.pow(cell.j - currentCell.y, 2));
            cell.hcost = (float) Math.sqrt(Math.pow(cell.i - endPosition.x, 2) + Math.pow(cell.j - endPosition.y, 2));
            cell.fcost = cell.gcost + cell.hcost;
            cell.isActive=true;
        }

        exploredPath.addAll(calculateCell);
        exploredPath.sort((a, b) -> Float.compare(a.fcost, b.fcost));
        for(GameCell cell : exploredPath){
            if(!cell.isExplored){
                currentCell = new Vector2(exploredPath.get(0).i, exploredPath.get(0).j);
            }
        }
        for(GameCell cell : gameCells){
            if(!cell.isExplored && cell.i==currentCell.x && cell.j==currentCell.y){
                cell.isExplored=true;
            }
        }
        calculateCell.clear();
    }

    private void initializeCell() {
        selectedPath=new Array<>();
        exploredPath=new Array<>();
        calculateCell=new Array<>();
        currentCell=new Vector2();
        for(GameCell cell : gameCells){
            cell.hcost=0;
            cell.fcost=0;
            cell.gcost=0;

        }
    }


}
