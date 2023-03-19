package com.wgsoft.game.gd.objects.game.cards;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import static com.wgsoft.game.gd.MyGdxGame.game;

public class Card extends Table {
    private int strength;
    public boolean ownsByPlayer;
    public void setStrength(int strength){
        this.strength = strength;
        label.setText(strength);
    }
    private String name;

    private Image bottom;
    private Image top;
    public void setTop(String name){
        top.setDrawable(game.skin, "card/top"+name);
    }
    private Label label;

    public Card(int strength, String name, boolean ownsByPlayer){
        this.strength = strength;
        this.name = name;
        this.ownsByPlayer = ownsByPlayer;
        setBackground(game.skin.getDrawable("card/background"));
        bottom = new Image(game.skin.getRegion("card/image/"+name));
        top = new Image(game.skin.getRegion("card/top/grey"));
        label = new Label(String.valueOf(strength), game.skin){{
            setAlignment(Align.center);
        }};
        add(new Stack(bottom, top, label));
        setTouchable(Touchable.disabled);
    }

    //Called when placed
    public void use(){}

    public Array<Card> aimCard(){
        boolean blue = false;
        final Array<Card> cards = new Array<>();

        for(Actor actor : ownsByPlayer ? game.gameScene.monsterTable.getChildren() : game.gameScene.playerTable.getChildren()){
            Card card = (Card) actor;
            if(!blue && card instanceof BlueCard && ((BlueCard) card).blueMethod(card)){
                blue = true;
                cards.clear();
            }
            if(aimable(card) && (!blue || card instanceof BlueCard && ((BlueCard) card).blueMethod(card))){
                 cards.add(card);
            }
        }

        return cards;
    }

    public Actor hit (float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    public boolean aimable(Card card){return true;}

    public void destroy(){
        if(ownsByPlayer){
            remove();
            game.mapScene.playerGraveyard.add(this);
            game.gameScene.playerScore -= getStrength();
            game.gameScene.playerScoreImage.currentScore.setText(game.gameScene.playerScore);
        }else{
            remove();
            game.gameScene.monsterScore -= getStrength();
            game.gameScene.monsterScoreImage.currentScore.setText(game.gameScene.monsterScore);
        }
    }

    public int getStrength(){
        return strength;
    }

    @Override
    public float getPrefWidth() {
        return 128f;
    }

    @Override
    public float getPrefHeight() {
        return 128f;
    }
}
