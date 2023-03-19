package com.wgsoft.game.gd.objects.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.gd.MyGdxGame.game;

public class ScoreImage extends Table {
    public Label currentScore, maxScore;
    public ScoreImage(){
        currentScore = new Label("", game.skin){{
            setAlignment(Align.center);
        }};
        add(currentScore).expand().fill();
        maxScore = new Label("", game.skin){{
            setAlignment(Align.center);
        }};
        add(maxScore).expand().fill();
        setBackground(game.skin.getDrawable("game/score"));
    }
}
