package com.corpsebane.game;

import com.badlogic.gdx.math.Rectangle;

public class Dungeon {
    public Rectangle dungeon;
    public float currentRadius=0;
    public boolean isConnected=false,isExplored=false;
    public Dungeon(Rectangle dungeon){
        this.dungeon=dungeon;
    }
}
