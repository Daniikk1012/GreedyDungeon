package com.wgsoft.game.gd.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.gd.objects.CameraMoveToAction;
import com.wgsoft.wgscene.Scene;
import com.wgsoft.wgscene.Transition;

import static com.wgsoft.game.gd.MyGdxGame.game;

public class MainMenuScene extends Scene {
    private final int BACKGROUND_LAYER = 0;
    private final int UI_LAYER = 1;

    //UI
    private Image title;
    private Table titleContainer;
    private TextButton buttonStart;
    private TextButton buttonSettings;
    private TextButton buttonExit;
    private Table container;

    public MainMenuScene() {
        super(2);
    }

    @Override
    public void show() {
        super.show();
        container.setLayoutEnabled(true);
    }

    @Override
    public void prepare() {
        if(getTransition().getNext() == this) {

            super.prepare();

            //BACKGROUND

            getStage(BACKGROUND_LAYER).addActor(new Actor() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    Color color = batch.getColor();
                    float r = color.r, g = color.g, b = color.b, a = color.a;
                    batch.setColor(r, g, b, a*parentAlpha);
                    batch.draw(game.skin.getRegion("main-menu/background"), getX(), getY(), getWidth(), getHeight());
                    batch.setColor(r, g, b, a);
                }

                @Override
                public void act(float delta) {
                    setWidth(Math.max(getStage().getWidth() + 32f, getStage().getHeight() + 32f));
                    setHeight(getWidth());
                    setPosition(0f, 0f, Align.center);
                }
            });

            getStage(BACKGROUND_LAYER).getCamera().position.set(0f, 0f, 0f);
            new Runnable() {
                @Override
                public void run() {
                    Runnable runnable = this;
                    getStage(BACKGROUND_LAYER).addAction(Actions.sequence(CameraMoveToAction.obtain(
                            MathUtils.random(-16f, 16f),
                            MathUtils.random(-16f, 16f),
                            MathUtils.random(2f, 3f),
                            Interpolation.fade
                    ), Actions.run(runnable)));
                }
            }.run();

            //UI

            title = new Image(game.skin.getRegion("main-menu/title"));

            titleContainer = new Table() {{
                add(title).size(416f, 96f);
                top();
            }};

            buttonStart = new TextButton(game.bundle.get("main-menu.button.start"), game.skin, "big") {{
                addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.mapScene.createGame();
                        game.setScreen(new Transition(game, game.mainMenuScene, game.mapScene, true, 1.5f));
                    }
                });
            }};

            buttonSettings = new TextButton(game.bundle.get("main-menu.button.settings"), game.skin, "big");

            buttonExit = new TextButton(game.bundle.get("main-menu.button.exit"), game.skin, "big") {{
                addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.app.exit();
                    }
                });
            }};

            container = new Table() {{
                setFillParent(true);
                add(titleContainer).expand().fill();
                row();
                add(buttonStart).expandX().size(416f, 96f).padTop(16f).padBottom(16f);
                row();
                add(buttonSettings).expandX().size(416f, 96f).padTop(16f).padBottom(16f);
                row();
                add(buttonExit).expandX().size(416f, 96f).padTop(16f).padBottom(16f);
                row();
                add().expand();
            }};

            getStage(UI_LAYER).addActor(container);

            container.validate();
            container.setLayoutEnabled(false);
        }

        Vector2 tmp = new Vector2();
        if (getTransition().getPrev() == this) {
            //UI
            title.localToParentCoordinates(title.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = title.getX();
            title.addAction(Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()/3f, Interpolation.exp10In));

            buttonStart.localToParentCoordinates(buttonStart.stageToLocalCoordinates(tmp.set(getStage(UI_LAYER).getWidth(), 0f)));
            tmp.y = buttonStart.getY();
            buttonStart.addAction(Actions.delay(getTransition().getDuration()*0.1f/3f, Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()*0.7f/3f, Interpolation.exp10In)));

            buttonSettings.localToParentCoordinates(buttonSettings.stageToLocalCoordinates(tmp.set(getStage(UI_LAYER).getWidth(), 0f)));
            tmp.y = buttonSettings.getY();
            buttonSettings.addAction(Actions.delay(getTransition().getDuration()*0.2f/3f, Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()*0.7f/3f, Interpolation.exp10In)));

            buttonExit.localToParentCoordinates(buttonExit.stageToLocalCoordinates(tmp.set(getStage(UI_LAYER).getWidth(), 0f)));
            tmp.y = buttonExit.getY();
            buttonExit.addAction(Actions.delay(getTransition().getDuration()*0.3f/3f, Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()*0.7f/3f, Interpolation.exp10In)));

        } else {
            //BACKGROUND
            getStage(BACKGROUND_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            //UI
            getStage(UI_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));

            title.localToParentCoordinates(title.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = title.getX();
            title.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()/1.5f, Actions.moveTo(title.getX(), title.getY(), getTransition().getDuration()/3f, Interpolation.exp10Out))));

            buttonStart.localToParentCoordinates(buttonStart.stageToLocalCoordinates(tmp.set(-buttonStart.getWidth(), 0f)));
            tmp.y = buttonStart.getY();
            buttonStart.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()*2.1f/3f, Actions.moveTo(buttonStart.getX(), buttonStart.getY(), getTransition().getDuration()*0.7f/3f, Interpolation.exp10Out))));

            buttonSettings.localToParentCoordinates(buttonSettings.stageToLocalCoordinates(tmp.set(-buttonSettings.getWidth(), 0f)));
            tmp.y = buttonSettings.getY();
            buttonSettings.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()*2.2f/3f, Actions.moveTo(buttonSettings.getX(), buttonSettings.getY(), getTransition().getDuration()*0.7f/3f, Interpolation.exp10Out))));

            buttonExit.localToParentCoordinates(buttonExit.stageToLocalCoordinates(tmp.set(-buttonExit.getWidth(), 0f)));
            tmp.y = buttonExit.getY();
            buttonExit.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()*2.3f/3f, Actions.moveTo(buttonExit.getX(), buttonExit.getY(), getTransition().getDuration()*0.7f/3f, Interpolation.exp10Out))));
        }
    }

    @Override
    public void resize(int width, int height) {
        if(width/Gdx.graphics.getDensity() > 480f && height/Gdx.graphics.getDensity() > 800f) {
            ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(1f / Gdx.graphics.getDensity());
            ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(1f / Gdx.graphics.getDensity());
        } else {
            if ((float) width / height > 480f / 800f) {
                ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(800f / height);
                ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(800f / height);
            } else {
                ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(480f / width);
                ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(480f / width);
            }
        }
        getStage(BACKGROUND_LAYER).getViewport().update(width, height);
        getStage(UI_LAYER).getViewport().update(width, height, true);
    }
}
