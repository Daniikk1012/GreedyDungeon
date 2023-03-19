package com.wgsoft.game.gd.objects.game.cards;

import com.badlogic.gdx.utils.Array;

public class Sword extends Card {
    public Sword(int strength, boolean ownsByPlayer) {
        super(strength, "sword", ownsByPlayer);
    }

    @Override
    public void use() {
        Array<Card> aimCards = aimCard();
        if(aimCards.size > 0) {
            Card card = aimCard().get(0);
            if (card != null) {
                card.destroy();
            }
        }
    }

    @Override
    public boolean aimable(Card card) {
        return card.getStrength() < getStrength();
    }
}
