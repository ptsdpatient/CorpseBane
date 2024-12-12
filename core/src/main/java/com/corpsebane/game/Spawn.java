package com.corpsebane.game;

public class Spawn {
    public int endRange,startRange,enemyCount,itemCount,memoCount,mercCount,npcCount,ventCount,utilityCount;
    public Spawn(int enemyCount,int startRange,int endRange,int itemCount,int memoCount,int mercCount, int npcCount,int ventCount,int utilityCount){
        this.enemyCount=enemyCount;
        this.startRange=startRange;
        this.endRange=endRange;
        this.itemCount=itemCount;
        this.memoCount=memoCount;
        this.mercCount=mercCount;
        this.npcCount=npcCount;
        this.ventCount=ventCount;
        this.utilityCount=utilityCount;
    }
}
