package com.wgsoft.game.gd.objects.game.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.wgsoft.game.gd.objects.game.cards.Card;

import static com.wgsoft.game.gd.MyGdxGame.game;

public abstract class Monster extends Actor {
    private TextureRegion region;
    private String name;
    public String getName(){
        return name;
    }
    public Monster(String name){
        this.name = name;
        region = game.skin.getRegion("monster/"+name);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        float r = color.r, g = color.g, b = color.b, a = color.a;
        batch.setColor(r, g, b, a*parentAlpha);
        batch.setColor(r, g, b, a);
    }

    @Override
    public void act(float delta) {
        setSize(240f, 240f);
        setPosition(0f, 0f, Align.center);
    }
    //Method must return new card taken by monster
    public abstract Card getCard();

    public abstract int getMin();

    public abstract int getMax();

    public abstract int getTableCardCount();
}
