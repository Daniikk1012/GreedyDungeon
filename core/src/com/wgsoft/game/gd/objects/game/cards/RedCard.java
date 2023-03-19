package com.wgsoft.game.gd.objects.game.cards;

//Reacts to player's overflow
public abstract class RedCard extends Card {
    public RedCard(int strength, String name, boolean ownsByPlayer){
        super(strength, name, ownsByPlayer);
        setTop("red");
    }

    //Returns true if handled, false otherwise
    public abstract boolean redMethod();
}
