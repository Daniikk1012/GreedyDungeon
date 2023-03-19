package com.wgsoft.wgscene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**Screen that allows you to easily implement screen transitions using scenes*/
public class Transition implements Screen {
    private Game game;
    private Scene prev, next;
    private boolean nextOnTop;
    private float time, duration;

    public Scene getPrev() {
        return prev;
    }

    public Scene getNext() {
        return next;
    }

    public float getTime() {
        return time;
    }

    public void addTime(float time) {
        this.time += time;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isNextOnTop() {
        return nextOnTop;
    }

    public Game getGame() {
        return game;
    }

    public Transition(Game game, Scene prev, Scene next, boolean nextOnTop, float duration){
        this.game = game;
        this.prev = prev;
        this.next = next;
        this.nextOnTop = nextOnTop;
        this.duration = duration;
    }

    @Override
    public void show() {
        time = 0f;
        if(prev != null) {
            prev.setTransition(this);
            prev.prepare();
        }
        if(next != null) {
            next.setTransition(this);
            next.prepare();
        }
    }

    public void update(float delta){
        if (nextOnTop) {
            if(prev != null) {
                prev.render(delta);
            }
            if(next != null) {
                next.render(delta);
            }
        } else {
            if(next != null) {
                next.render(delta);
            }
            if(prev != null) {
                prev.render(delta);
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        time += delta;
        if(time >= duration) {
            game.setScreen(next);
        }
    }

    @Override
    public void resize(int width, int height) {
        if(prev != null) {
            prev.resize(width, height);
        }
        if(next != null) {
            next.resize(width, height);
        }
    }

    @Override
    public void pause() {
        if(prev != null) {
            prev.pause();
        }
        if(next != null) {
            next.pause();
        }
    }

    @Override
    public void resume() {
        if(prev != null) {
            prev.resume();
        }
        if(next != null) {
            next.resume();
        }
    }

    @Override
    public void hide() {
        if(prev != null) {
            prev.setTransition(null);
        }
        if(next != null) {
            next.setTransition(null);
        }
    }

    @Override
    public void dispose() {}
}
