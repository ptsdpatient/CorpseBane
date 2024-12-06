package com.corpsebane.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public  class Methods {

    public static void print(String value){
        Gdx.app.log("Game",value);
    }
    public static void print(Vector3 value){
        Gdx.app.log("Game",value+"");
    }
    public static void print(int value){
        Gdx.app.log("Game",value+"");
    }
    public static void print(String tag,String value){
        Gdx.app.log(tag,value);
    }
    public static FileHandle load(String value){
        return Gdx.files.internal(value);
    }



}
