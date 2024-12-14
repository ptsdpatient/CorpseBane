package com.corpsebane.game;

import static com.corpsebane.game.Methods.load;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

public class TextButton {
    BitmapFont font;
    String name;
    Rectangle bounds;
    float time=0;
    TextButton(String name, Rectangle bounds){
        this.font=new BitmapFont(load("font.fnt"));
        font.getData().setScale(0.7f);

        this.name=name;
        this.bounds=bounds;
    }
    public void render(SpriteBatch batch,float delta,boolean active){
        time+=delta;
        if(active) font.getData().setScale(0.7f + (0.75f - 0.7f) * (0.5f + 0.5f * (float) Math.sin(time*7f)));
        font.draw(batch,name,bounds.x,bounds.y+bounds.height);
    }
}
