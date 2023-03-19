package com.wgsoft.game.gd.objects.game.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.wgsoft.game.gd.objects.game.cards.Card;
import com.wgsoft.game.gd.objects.game.cards.Sword;

public class Dog extends Monster {
    public Dog(){
        super("dog");
    }
    @Override
    public Card getCard() {
        return new Sword(MathUtils.random(10), false);
    }

    @Override
    public int getMin() {
        return 15;
    }

    @Override
    public int getMax() {
        return 21;
    }

    @Override
    public int getTableCardCount() {
        return 4;
    }
}
