package com.wgsoft.game.gd.objects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class CameraMoveToAction extends TemporalAction {
    static {
        Pools.set(CameraMoveToAction.class, new Pool<CameraMoveToAction>() {
            @Override
            protected CameraMoveToAction newObject() {
                return new CameraMoveToAction();
            }
        });
    }

    public static CameraMoveToAction obtain(float x, float y, float duration, Interpolation interpolation){
        Pool<CameraMoveToAction> cameraMoveToActionPool = Pools.get(CameraMoveToAction.class);
        CameraMoveToAction cameraMoveToAction = cameraMoveToActionPool.obtain();
        cameraMoveToAction.setPool(cameraMoveToActionPool);
        cameraMoveToAction.setX(x);
        cameraMoveToAction.setY(y);
        cameraMoveToAction.setDuration(duration);
        cameraMoveToAction.setInterpolation(interpolation);
        return cameraMoveToAction;
    }

    private float startX, startY, endX, endY;

    public void setX(float x) {
        endX = x;
    }

    public void setY(float y) {
        endY = y;
    }

    public float getX() {
        return endX;
    }

    public float getY() {
        return endY;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    @Override
    protected void begin() {
        startX = getActor().getStage().getCamera().position.x;
        startY = getActor().getStage().getCamera().position.y;
    }

    @Override
    protected void update(float percent) {
        getActor().getStage().getCamera().position.set(startX+(endX-startX)*percent, startY+(endY-startY)*percent, getActor().getStage().getCamera().position.z);
    }
}
