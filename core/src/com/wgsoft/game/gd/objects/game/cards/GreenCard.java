package com.wgsoft.game.gd.objects.game.cards;

//Reacts to enemy's overflow
public abstract class GreenCard extends Card {
    public GreenCard(int strength, String name, boolean ownsByPlayer) {
        super(strength, name, ownsByPlayer);
        setTop("green");
    }

    //Returns true if handles, false otherwise
    public abstract boolean greenMethod();
}
