package com.wgsoft.game.gd.objects.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.wgsoft.wgscene.Transition;

import static com.wgsoft.game.gd.MyGdxGame.game;

public class Room extends Actor implements Comparable<Room>{
    public enum Direction{
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        NONE
    }
    public enum Type{
        MEGA_BOSS,
        BOSS,
        SHOP,
        CHEST,
        SECRET,
        REGULAR,
        MAGIC,
        REROLL,
        LEVEL_UP,
        TRAPDOOR,
        LADDERS,
        NONE
    }
    public Room left, right, top, bottom;
    private Direction direction;
    private int distance;
    public int x, y;
    public Type type;
    public boolean pressed;

    public Room(Direction direction, Room from){
        this.direction = direction;
        type = Type.NONE;
        if(direction != Direction.NONE){
            x = from.x;
            y = from.y;
            distance = from.distance+1;
        }
        switch (direction){
            case LEFT:
                left = from;
                x++;
                break;
            case RIGHT:
                x--;
                right = from;
                break;
            case TOP:
                y--;
                top = from;
                break;
            case BOTTOM:
                y++;
                bottom = from;
                break;
        }
        setSize(64f, 64f);
        setPosition(x*getWidth(), y*getHeight(), Align.center);
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return pressed = pointer == 0;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                pressed = x > 0f && x < getWidth() && y > 0f && y < getHeight();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(pressed){
                    pressed = false;
                    //TODO Other variants
                    if(!game.mapScene.byButton) {
                        switch (type) {
                            case REGULAR:
                                game.gameScene.createGame();
                                game.setScreen(new Transition(game, game.mapScene, game.gameScene, true, 1.5f));
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        float r = color.r, g = color.g, b = color.b, a = color.a;
        batch.setColor(r, g, b, color.a*parentAlpha);
        batch.draw(game.skin.getRegion(pressed?"button/room/down":"button/room/up"), getX(), getY(), getWidth(), getHeight());
        switch (type){
            case NONE:
                batch.draw(game.skin.getRegion("button/room/icon/none"), getX(), getY(), getWidth(), getHeight());
                break;
            case REGULAR:
                batch.draw(game.skin.getRegion("button/room/icon/regular"), getX(), getY(), getWidth(), getHeight());
                break;
            case REROLL:
                batch.draw(game.skin.getRegion("button/room/icon/reroll"), getX(), getY(), getWidth(), getHeight());
                break;
            case LEVEL_UP:
                batch.draw(game.skin.getRegion("button/room/icon/level-up"), getX(), getY(), getWidth(), getHeight());
                break;
            case SHOP:
                batch.draw(game.skin.getRegion("button/room/icon/shop"), getX(), getY(), getWidth(), getHeight());
                break;
            case SECRET:
                batch.draw(game.skin.getRegion("button/room/icon/secret"), getX(), getY(), getWidth(), getHeight());
                break;
            case CHEST:
                batch.draw(game.skin.getRegion("button/room/icon/chest"), getX(), getY(), getWidth(), getHeight());
                break;
            case MAGIC:
                batch.draw(game.skin.getRegion("button/room/icon/magic"), getX(), getY(), getWidth(), getHeight());
                break;
            case BOSS:
                batch.draw(game.skin.getRegion("button/room/icon/boss"), getX(), getY(), getWidth(), getHeight());
                break;
            case MEGA_BOSS:
                batch.draw(game.skin.getRegion("button/room/icon/mega-boss"), getX(), getY(), getWidth(), getHeight());
                break;
            case LADDERS:
                batch.draw(game.skin.getRegion("button/room/icon/ladders"), getX(), getY(), getWidth(), getHeight());
                break;
            case TRAPDOOR:
                batch.draw(game.skin.getRegion("button/room/icon/trapdoor"), getX(), getY(), getWidth(), getHeight());
                break;
        }
        batch.setColor(r, g, b, a);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible && type != Type.BOSS && type != Type.REGULAR){
            if(direction != Direction.LEFT && left != null && left.type != Type.SECRET){
                left.setVisible(true);
            }
            if(direction != Direction.RIGHT && right != null && right.type != Type.SECRET){
                right.setVisible(true);
            }
            if(direction != Direction.TOP && top != null && top.type != Type.SECRET){
                top.setVisible(true);
            }
            if(direction != Direction.BOTTOM && bottom != null && bottom.type != Type.SECRET){
                bottom.setVisible(true);
            }
        }
    }

    public void showSecrets(){
        if(direction != Direction.LEFT && left != null && left.type == Type.SECRET){
            left.setVisible(true);
        }
        if(direction != Direction.RIGHT && right != null && right.type == Type.SECRET){
            right.setVisible(true);
        }
        if(direction != Direction.TOP && top != null && top.type == Type.SECRET){
            top.setVisible(true);
        }
        if(direction != Direction.BOTTOM && bottom != null && bottom.type == Type.SECRET){
            bottom.setVisible(true);
        }
    }

    @Override
    public String toString() {
        return "("+type+" "+x+","+y+":"+direction+")";
    }

    @Override
    public int compareTo(Room room) {
        return room.distance-distance;
    }

    public static Room at(int x, int y){
        Array.ArrayIterator<Room> iterator = new Array.ArrayIterator<Room>(game.mapScene.rooms);
        for(Room room : iterator){
            if(room.x == x && room.y == y){
                return room;
            }
        }
        return null;
    }
}
