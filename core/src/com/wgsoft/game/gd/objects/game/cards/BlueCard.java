package com.wgsoft.game.gd.objects.game.cards;

//Makes itself aim for other card
public abstract class BlueCard extends Card {
    public BlueCard(int strength, String name, boolean ownsByPlayer) {
        super(strength, name, ownsByPlayer);
        setTop("blue");
    }

    public abstract boolean blueMethod(Card card);
}
