package com.wgsoft.game.gd.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.gd.objects.CameraMoveToAction;
import com.wgsoft.game.gd.objects.game.cards.Card;
import com.wgsoft.game.gd.objects.game.cards.Sword;
import com.wgsoft.game.gd.objects.map.Room;
import com.wgsoft.wgscene.Scene;
import com.wgsoft.wgscene.Transition;

import static com.wgsoft.game.gd.MyGdxGame.game;

public class MapScene extends Scene {
    private static final int BACKGROUND_LAYER = 0;
    private static final int GAME_LAYER = 1;
    private static final int UI_LAYER = 2;

    //GAME
    public Array<Room> rooms;

    public int maxPlayerScore;
    public int playerRoll;
    public int playerTableCardCount;
    public int playerHandCardCount;
    public int money;
    public Array<Card> playerDeck;
    public Array<Card> playerGraveyard;

    public boolean byButton = false;

    //UI
    private ImageButton pause;
    private ImageButton graveyard;
    private Label title;
    private ImageButton deck;
    private ImageButton map;
    private Table topBar;
    private Table container;

    public MapScene() {
        super(3);
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
                    batch.draw(game.skin.getRegion("map/background"), getX(), getY(), getWidth(), getHeight());
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

            //GAME
            getStage(GAME_LAYER).getCamera().position.set(0f, 0f, 0f);
            Array.ArrayIterator<Room> iterator = new Array.ArrayIterator<Room>(rooms);
            for(Room room : iterator){
                room.setVisible(false);
                getStage(GAME_LAYER).addActor(room);
            }
            rooms.get(rooms.size-1).setVisible(true);
            //USES UI_LAYER
            getStage(UI_LAYER).addActor(new Actor(){
                {
                    addListener(new ActorGestureListener(){
                        private Room room;
                        private Vector2 tmp = new Vector2();
                        @Override
                        public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            tmp.set(x, y);
                            localToStageCoordinates(tmp);
                            getStage().stageToScreenCoordinates(tmp);
                            MapScene.this.getStage(GAME_LAYER).screenToStageCoordinates(tmp);
                            room = (Room)MapScene.this.getStage(GAME_LAYER).hit(tmp.x, tmp.y, true);
                            if(room != null){
                                InputEvent inputEvent = new InputEvent();
                                inputEvent.setStage(MapScene.this.getStage(GAME_LAYER));
                                inputEvent.setStageX(tmp.x);
                                inputEvent.setStageY(tmp.y);
                                inputEvent.setType(InputEvent.Type.touchDown);
                                room.fire(inputEvent);
                            }
                        }

                        @Override
                        public void zoom(InputEvent event, float initialDistance, float distance) {
                            if(room != null){
                                room.pressed = false;
                                InputEvent inputEvent = new InputEvent();
                                inputEvent.setStage(MapScene.this.getStage(GAME_LAYER));
                                inputEvent.setType(InputEvent.Type.touchUp);
                                room.fire(event);
                                room = null;
                            }
                        }

                        @Override
                        public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                            if(room != null){
                                room.pressed = false;
                                InputEvent inputEvent = new InputEvent();
                                inputEvent.setStage(MapScene.this.getStage(GAME_LAYER));
                                inputEvent.setType(InputEvent.Type.touchUp);
                                room.fire(event);
                                room = null;
                            }
                            MapScene.this.getStage(GAME_LAYER).getCamera().position.x -= deltaX;
                            MapScene.this.getStage(GAME_LAYER).getCamera().position.y -= deltaY;
                        }

                        @Override
                        public void tap(InputEvent event, float x, float y, int count, int button) {
                            if(count == 1) {
                                if (room != null) {
                                    tmp.set(x, y);
                                    localToStageCoordinates(tmp);
                                    getStage().stageToScreenCoordinates(tmp);
                                    InputEvent inputEvent = new InputEvent();
                                    MapScene.this.getStage(GAME_LAYER).screenToStageCoordinates(tmp);
                                    inputEvent.setStageX(tmp.x);
                                    inputEvent.setStageY(tmp.y);
                                    inputEvent.setStage(MapScene.this.getStage(GAME_LAYER));
                                    inputEvent.setType(InputEvent.Type.touchDragged);
                                    room.fire(event);
                                    inputEvent.setType(InputEvent.Type.touchUp);
                                    room.fire(event);
                                    room = null;
                                }
                            }else{
                                MapScene.this.getStage(GAME_LAYER).addAction(CameraMoveToAction.obtain(0f, 0f, 1f, Interpolation.exp10Out));
                            }
                        }
                        @Override
                        public boolean longPress(Actor actor, float x, float y) {
                            if(room != null){
                                InputEvent inputEvent = new InputEvent();
                                room.pressed = false;
                                inputEvent.setStage(MapScene.this.getStage(GAME_LAYER));
                                inputEvent.setType(InputEvent.Type.touchUp);
                                room.fire(inputEvent);
                                if(room.type != Room.Type.BOSS && room.type != Room.Type.REGULAR){
                                    room.showSecrets();
                                }
                                room = null;
                            }
                            return true;
                        }
                    });
                }
                @Override
                public void act(float delta) {
                    setSize(getStage().getWidth(), getStage().getHeight());
                }
            });

            //UI
            pause = new ImageButton(game.skin, "pause"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            graveyard = new ImageButton(game.skin, "graveyard"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            title = new Label("LEVEL: 1", game.skin) {{ //TODO Text replace
                setAlignment(Align.center);
            }};
            deck = new ImageButton(game.skin, "deck"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            map = new ImageButton(game.skin, "map"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
                if(getTransition().getPrev() != game.gameScene){
                    setDisabled(true);
                }else{
                    addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            game.setScreen(new Transition(game, game.mapScene, game.gameScene, true, 1.5f));
                        }
                    });
                }
            }};

            topBar = new Table() {{
                add(pause).size(64f);
                add(graveyard).size(64f);
                add(title).expandX().fillX();
                add(deck).size(64f);
                add(map).size(64f);
            }};

            container = new Table() {{
                setFillParent(true);
                add(topBar).expandX().fillX();
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
            topBar.localToParentCoordinates(graveyard.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = topBar.getX();
            topBar.addAction(Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()/3f, Interpolation.exp10In));
        } else {
            //BACKGROUND
            getStage(BACKGROUND_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            //GAME
            getStage(GAME_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            //UI
            getStage(UI_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            topBar.localToParentCoordinates(topBar.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = topBar.getX();
            topBar.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()/1.5f, Actions.moveTo(topBar.getX(), topBar.getY(), getTransition().getDuration()/3f, Interpolation.exp10Out))));
        }
    }

    public void createGame(){
        maxPlayerScore = 21;
        playerRoll = 1;
        playerTableCardCount = 2;
        playerHandCardCount = 1;
        money = 0;
        playerDeck = new Array<Card>(){{
            addAll(new Sword(7, true), new Sword(6, true), new Sword(8, true), new Sword(9, true));
            shuffle();
        }};
        playerGraveyard = new Array<>();
        createLevel();
    }

    @Override
    public void resize(int width, int height) {
        if(width/ Gdx.graphics.getDensity() > 480f && height/Gdx.graphics.getDensity() > 800f) {
            ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(1f / Gdx.graphics.getDensity());
            ((ScreenViewport) getStage(GAME_LAYER).getViewport()).setUnitsPerPixel(1f / Gdx.graphics.getDensity());
            ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(1f / Gdx.graphics.getDensity());
        } else {
            if ((float) width / height > 480f / 800f) {
                ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(800f / height);
                ((ScreenViewport) getStage(GAME_LAYER).getViewport()).setUnitsPerPixel(800f / height);
                ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(800f / height);
            } else {
                ((ScreenViewport) getStage(BACKGROUND_LAYER).getViewport()).setUnitsPerPixel(480f / width);
                ((ScreenViewport) getStage(GAME_LAYER).getViewport()).setUnitsPerPixel(480f / width);
                ((ScreenViewport) getStage(UI_LAYER).getViewport()).setUnitsPerPixel(480f / width);
            }
        }
        getStage(BACKGROUND_LAYER).getViewport().update(width, height);
        getStage(GAME_LAYER).getViewport().update(width, height);
        getStage(UI_LAYER).getViewport().update(width, height, true);
    }

    private void createLevel(){
        int megaBossCount,
                magicCount,
                bossCount,
                shopCount,
                chestCount,
                rerollCount,
                levelUpCount,
                regularCount,
                secretCount;
        int roomCount;
        int doorCount;
        int canceledDoorCount;
        rooms = new Array<Room>();
        rooms.add(new Room(Room.Direction.NONE, null));
        doorCount = 4;
        roomCount = 0;
        roomCount += megaBossCount = MathUtils.random(0, 1);
        roomCount += magicCount = 1;
        roomCount += bossCount = MathUtils.random(1, 2);
        roomCount += shopCount = MathUtils.random(1, 3);
        roomCount += chestCount = MathUtils.random(2, 3);
        roomCount += rerollCount = MathUtils.random(0, 2);
        roomCount += levelUpCount = MathUtils.random(0, 1);
        roomCount += regularCount = MathUtils.random(4, 13);
        secretCount = MathUtils.random(1, 2);
        while(roomCount > 0){
            canceledDoorCount = 0;
            Array.ArrayIterator<Room> iterator = new Array.ArrayIterator<Room>(rooms);
            for(Room room : iterator){
                if(Room.at(room.x-1, room.y) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.left = new Room(Room.Direction.RIGHT, room);
                        rooms.add(room.left);
                        roomCount--;
                        if(Room.at(room.left.x-1, room.left.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.left.x+1, room.left.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.left.x, room.left.y+1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.left.x, room.left.y-1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x+1, room.y) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.right = new Room(Room.Direction.LEFT, room);
                        rooms.add(room.right);
                        roomCount--;
                        if(Room.at(room.right.x-1, room.right.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.right.x+1, room.right.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.right.x, room.right.y+1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.right.x, room.right.y-1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x, room.y+1) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.top = new Room(Room.Direction.BOTTOM, room);
                        rooms.add(room.top);
                        roomCount--;
                        if(Room.at(room.top.x-1, room.top.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.top.x+1, room.top.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.top.x, room.top.y+1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.top.x, room.top.y-1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x, room.y-1) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.bottom = new Room(Room.Direction.TOP, room);
                        rooms.add(room.bottom);
                        roomCount--;
                        if(Room.at(room.bottom.x-1, room.bottom.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x+1, room.bottom.y) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x, room.bottom.y+1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x, room.bottom.y-1) == null){
                            doorCount++;
                        }else{
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
            }
        }
        rooms.shuffle();
        rooms.sort();
        int i = 0;
        while(megaBossCount > 0){
            rooms.get(i).type = Room.Type.MEGA_BOSS;
            i++;
            megaBossCount--;
        }
        while(magicCount > 0){
            rooms.get(i).type = Room.Type.MAGIC;
            i++;
            magicCount--;
        }
        while(bossCount > 0){
            rooms.get(i).type = Room.Type.BOSS;
            i++;
            bossCount--;
        }
        while(shopCount > 0){
            rooms.get(i).type = Room.Type.SHOP;
            i++;
            shopCount--;
        }
        while(chestCount + rerollCount + levelUpCount + regularCount > 0){
            int rand = MathUtils.random(1, chestCount + rerollCount + levelUpCount + regularCount);
            if(rand <= chestCount){
                rooms.get(i).type = Room.Type.CHEST;
                chestCount--;
            }else if((rand -= chestCount) <= rerollCount){
                rooms.get(i).type = Room.Type.REROLL;
                rerollCount--;
            }else if(rand - rerollCount <= levelUpCount){
                rooms.get(i).type = Room.Type.LEVEL_UP;
                levelUpCount--;
            }else{
                rooms.get(i).type = Room.Type.REGULAR;
                regularCount--;
            }
            i++;
        }
        while(secretCount > 0){
            canceledDoorCount = 0;
            Array.ArrayIterator<Room> iterator = new Array.ArrayIterator<Room>(rooms);
            for(Room room : iterator){
                if(room.type == Room.Type.SECRET){
                    continue;
                }
                if(Room.at(room.x-1, room.y) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.left = new Room(Room.Direction.RIGHT, room);
                        room.left.type = Room.Type.SECRET;
                        rooms.add(room.left);
                        if(Room.at(room.left.x-1, room.left.y) != null){
                            doorCount--;
                        }
                        if(Room.at(room.left.x+1, room.left.y) != null){
                            doorCount--;
                        }
                        if(Room.at(room.left.x, room.left.y+1) != null){
                            doorCount--;
                        }
                        if(Room.at(room.left.x, room.left.y-1) != null){
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x+1, room.y) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.right = new Room(Room.Direction.LEFT, room);
                        room.right.type = Room.Type.SECRET;
                        rooms.add(room.right);
                        if(Room.at(room.right.x-1, room.right.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.right.x+1, room.right.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.right.x, room.right.y+1) == null){
                            doorCount--;
                        }
                        if(Room.at(room.right.x, room.right.y-1) == null){
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x, room.y+1) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.top = new Room(Room.Direction.BOTTOM, room);
                        room.top.type = Room.Type.SECRET;
                        rooms.add(room.top);
                        if(Room.at(room.top.x-1, room.top.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.top.x+1, room.top.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.top.x, room.top.y+1) == null){
                            doorCount--;
                        }
                        if(Room.at(room.top.x, room.top.y-1) == null){
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
                if(Room.at(room.x, room.y-1) == null){
                    if(MathUtils.randomBoolean(1f/(doorCount-canceledDoorCount))){
                        room.bottom = new Room(Room.Direction.TOP, room);
                        room.bottom.type = Room.Type.SECRET;
                        rooms.add(room.bottom);
                        if(Room.at(room.bottom.x-1, room.bottom.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x+1, room.bottom.y) == null){
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x, room.bottom.y+1) == null){
                            doorCount--;
                        }
                        if(Room.at(room.bottom.x, room.bottom.y-1) == null){
                            doorCount--;
                        }
                        break;
                    }else{
                        canceledDoorCount++;
                    }
                }
            }
            secretCount--;
        }
        rooms.sort();
    }

    @Override
    public void hide() {
        super.hide();
        byButton = false;
    }
}
