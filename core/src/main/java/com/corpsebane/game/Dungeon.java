package com.corpsebane.game;

import com.badlogic.gdx.math.Rectangle;

public class Dungeon {
    public Rectangle dungeon;
    public boolean isConnected=false;
    public Dungeon(Rectangle dungeon){
        this.dungeon=dungeon;
    }
}
