package com.wgsoft.wgscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**Some methods can be called while transition*/
public class Scene implements Screen {
    private Stage[] stages;
    private InputProcessor processor;
    private Batch batch;
    private Transition transition;

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    /**Here you prepare Scene to be shown(At the beginning of transition)
     * This method automatically clears all stages*/
    public void prepare(){
        for(int i = 0; i < stages.length; i++){
            stages[i].clear();
        }
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Stage getStage(int index){
        return stages[index];
    }

    public InputProcessor getProcessor(){
        return processor;
    }

    public Batch getBatch(){
        return batch;
    }

    public Scene(int layers){
        stages = new Stage[layers];
        batch = new SpriteBatch();
        for(int i = 0; i < layers; i++){
            stages[i] = new Stage(new ScreenViewport(), batch);
        }
        processor = new InputMultiplexer();
        for(int i = layers-1; i >= 0; i--) {
            ((InputMultiplexer) processor).addProcessor(stages[i]);
        }
    }

    /**Called after transition ends*/
    @Override
    public void show() {
        Gdx.input.setInputProcessor(processor);
    }

    @Override
    public void render(float delta) {
        act(delta);
        draw();
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void act(float delta){
        for(int i = 0; i < stages.length; i++){
            stages[i].act(delta);
        }
    }

    public void draw(){
        for(int i = 0; i < stages.length; i++){
            stages[i].draw();
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /**Hide method of the screen
     * Not recommended to override if you use screen transitions*/
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
