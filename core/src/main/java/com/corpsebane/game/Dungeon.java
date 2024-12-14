package com.corpsebane.game;

import com.badlogic.gdx.math.Rectangle;

public class Dungeon {
    public Rectangle dungeon;
    public float currentRadius=0;
    public boolean isConnected=false,isExplored=false;
    public int id;
    public Dungeon(Rectangle dungeon,int id){
        this.id=id;
        this.dungeon=dungeon;
    }
}
