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
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.gd.objects.CameraMoveToAction;
import com.wgsoft.game.gd.objects.game.cards.Card;
import com.wgsoft.game.gd.objects.game.cards.GreenCard;
import com.wgsoft.game.gd.objects.game.cards.RedCard;
import com.wgsoft.game.gd.objects.game.monsters.Dog;
import com.wgsoft.game.gd.objects.game.monsters.Monster;
import com.wgsoft.game.gd.objects.game.ScoreImage;
import com.wgsoft.wgscene.Scene;
import com.wgsoft.wgscene.Transition;

import static com.wgsoft.game.gd.MyGdxGame.game;

//TODO Game elements
public class GameScene extends Scene {
    private static final int BACKGROUND_LAYER = 0;
    private static final int UI_LAYER = 1;

    //BACKGROUND
    private Monster monster; //GAME

    //GAME
    public int playerScore, maxPlayerScore;
    public int monsterScore, maxMonsterScore, minMonsterScore, monsterTableCardCount;
    private DragAndDrop dragAndDrop;
    private int draggingIndex;

    //UI
    private ImageButton pause;
    private ImageButton graveyard;
    private Label title;
    private ImageButton deck;
    private ImageButton map;
    private Table topBar;
    private Image monsterTableCardInfoImage;
    private Label monsterTableCardInfo;
    public ScoreImage monsterScoreImage;
    private Label monsterMinInfo;
    private Image monsterMinInfoImage;
    private Table monsterInfo;
    public HorizontalGroup monsterTable; //GAME
    private ScrollPane monsterTableScroller;
    private Image playerMoneyInfoImage;
    private Label playerMoneyInfo;
    private Label playerRollInfo;
    private Image playerRollInfoImage;
    private Image playerTableCardInfoImage;
    private Label playerTableCardInfo;
    private Label playerHandCardInfo;
    private Image playerHandCardInfoImage;
    private Table playerInfo;
    public HorizontalGroup playerTable; //GAME
    private ScrollPane playerTableScroller;
    private ImageButton stop;
    private ImageButton info;
    public ScoreImage playerScoreImage;
    private ImageButton magic;
    private ImageButton add;
    private Table gameControl;
    public HorizontalGroup playerHand; //GAME
    private ScrollPane playerHandScroller;
    private Table container;

    public GameScene() {
        super(2);
    }

    @Override
    public void show() {
        super.show();
        container.setLayoutEnabled(true);
    }

    @Override
    public void prepare() {
        if(getTransition().getNext() == this){
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
            pause = new ImageButton(game.skin, "pause"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            graveyard = new ImageButton(game.skin, "graveyard"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            title = new Label(monster.getName().substring(0, 1).toUpperCase()+monster.getName().substring(1), game.skin) {{
                setAlignment(Align.center);
            }};
            deck = new ImageButton(game.skin, "deck"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            map = new ImageButton(game.skin, "map"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
                addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.mapScene.byButton = true;
                        game.setScreen(new Transition(game, game.gameScene, game.mapScene, true, 1.5f));
                    }
                });
            }};

            topBar = new Table() {{
                add(pause).size(64f);
                add(graveyard).size(64f);
                add(title).expandX().fillX();
                add(deck).size(64f);
                add(map).size(64f);
            }};

            monsterTableCardInfoImage = new Image(game.skin, "game/icon/table");

            monsterTableCardInfo = new Label("", game.skin);

            monsterScoreImage = new ScoreImage();

            monsterMinInfo = new Label("", game.skin){{
                setAlignment(Align.center|Align.right);
            }};

            monsterMinInfoImage = new Image(game.skin, "game/icon/min");

            monsterInfo = new Table(){{
                add(monsterTableCardInfoImage).size(64f);
                add(monsterTableCardInfo).height(64f).expandX().fill();
                add(monsterScoreImage).size(128f, 64f);
                add(monsterMinInfo).height(64f).expandX().fill();
                add(monsterMinInfoImage).size(64f);
            }};

            monsterTableScroller = new ScrollPane(monsterTable);

            playerMoneyInfoImage = new Image(game.skin, "game/icon/money");

            playerMoneyInfo = new Label("", game.skin);

            playerRollInfo = new Label("", game.skin){{
                setAlignment(Align.center | Align.right);
            }};

            playerRollInfoImage = new Image(game.skin, "game/icon/roll");

            playerTableCardInfoImage = new Image(game.skin, "game/icon/table");

            playerTableCardInfo = new Label("", game.skin);

            playerHandCardInfo = new Label("", game.skin){{
                setAlignment(Align.center | Align.right);
            }};

            playerHandCardInfoImage = new Image(game.skin, "game/icon/hand");

            playerInfo = new Table(){{
                add(playerMoneyInfoImage).size(64f);
                add(playerMoneyInfo).height(64f).expandX().fill();
                add(playerRollInfo).height(64f).expandX().fill();
                add(playerRollInfoImage).size(64f);
                row();
                add(playerTableCardInfoImage).size(64f);
                add(playerTableCardInfo).height(64f).expandX().fill();
                add(playerHandCardInfo).height(64f).expandX().fill();
                add(playerHandCardInfoImage).size(64f);
            }};

            playerTableScroller = new ScrollPane(playerTable);

            stop = new ImageButton(game.skin, "stop"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            info = new ImageButton(game.skin, "info"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            playerScoreImage = new ScoreImage();
            magic = new ImageButton(game.skin, "magic"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};
            add = new ImageButton(game.skin, "add"){{
                getImage().setScaling(Scaling.stretch);
                getImageCell().expand().fill();
            }};

            gameControl = new Table(){{
                add(stop).size(64f);
                add(info).size(64f);
                add(playerScoreImage).expandX().size(128f, 64f);
                add(magic).size(64f);
                add(add).size(64f);
            }};

            playerHandScroller = new ScrollPane(playerHand);

            container = new Table(){{
                setFillParent(true);
                setDebug(true, true);
                add(topBar).expandX().fillX();
                row();
                add(monsterInfo).expandX().fill().height(64f);
                row();
                add(monsterTableScroller).expandX().height(128f).fill();
                row();
                add(playerInfo).expandX().fill().height(192f);
                row();
                add().expand();
                row();
                add(playerTableScroller).expandX().height(128f).fill();
                row();
                add(gameControl).expandX().fillX();
                row();
                add(playerHandScroller).expandX().height(128f).fill();
            }};

            getStage(UI_LAYER).addActor(container);

            //GAME
            dragAndDrop = new DragAndDrop();
            dragAndDrop.addSource(new DragAndDrop.Source(playerHandScroller) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Actor actor;
                    if((actor = playerHand.hit(x, y, false)) == null){
                        return null;
                    }
                    draggingIndex = actor.getZIndex();
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setDragActor(actor);
                    return payload;
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    if(payload != null && target == null){
                        playerHand.addActorAt(draggingIndex, payload.getDragActor());
                    }
                }
            });

            dragAndDrop.addTarget(new DragAndDrop.Target(playerTableScroller) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return true;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    playerTable.addActor(payload.getDragActor());
                    ((Card)payload.getDragActor()).use();
                    playerScore += ((Card)payload.getDragActor()).getStrength();
                    playerScoreImage.currentScore.setText(playerScore);
                    checkPlayer();
                    monsterTurn();
                }
            });

            setUI();

            container.validate();
            container.setLayoutEnabled(false);
        }

        Vector2 tmp = new Vector2();
        if(getTransition().getPrev() == this){
            //UI
            topBar.localToParentCoordinates(graveyard.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = topBar.getX();
            topBar.addAction(Actions.moveTo(tmp.x, tmp.y, getTransition().getDuration()/3f, Interpolation.exp10In));
        }else{
            //BACKGROUND
            getStage(BACKGROUND_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            //UI
            getStage(UI_LAYER).addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(getTransition().getDuration()/3f, Actions.alpha(1f, getTransition().getDuration()/3f, Interpolation.exp10Out))));
            topBar.localToParentCoordinates(topBar.stageToLocalCoordinates(tmp.set(0f, getStage(UI_LAYER).getHeight())));
            tmp.x = topBar.getX();
            topBar.addAction(Actions.sequence(Actions.moveTo(tmp.x, tmp.y), Actions.delay(getTransition().getDuration()/1.5f, Actions.moveTo(topBar.getX(), topBar.getY(), getTransition().getDuration()/3f, Interpolation.exp10Out))));
        }
    }

    private void monsterTurn(){
        if(monsterScore < minMonsterScore){
            Card card = monster.getCard();
            monsterTable.addActor(card);
            card.use();
            monsterScore += card.getStrength();
            monsterScoreImage.currentScore.setText(monsterScore);
        }
    }

    private void checkMonster(){
        if(monsterScore > maxMonsterScore){
            for(Actor actor : playerTable.getChildren()){
                if(actor instanceof GreenCard){
                    GreenCard card = (GreenCard)actor;
                    if(card.greenMethod()){
                        card.destroy();
                        break;
                    }
                }
            }
            boolean found;
            while(monsterScore > maxMonsterScore) {
                found = false;
                for (Actor actor : monsterTable.getChildren()) {
                    if (actor instanceof RedCard) {
                        RedCard card = (RedCard) actor;
                        if (card.redMethod()) {
                            card.destroy();
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    break;
                }
            }
            if(monsterScore > maxMonsterScore){
                System.out.println("PLAYER WON");
                //TODO End of battle
            }
        }
    }

    private void checkPlayer(){
        if(playerScore > maxPlayerScore){
            for(Actor actor : monsterTable.getChildren()){
                if(actor instanceof GreenCard){
                    GreenCard card = (GreenCard)actor;
                    if(card.greenMethod()){
                        card.destroy();
                        break;
                    }
                }
            }
            boolean found;
            while(playerScore > maxPlayerScore) {
                found = false;
                for (Actor actor : playerTable.getChildren()) {
                    if (actor instanceof RedCard) {
                        RedCard card = (RedCard) actor;
                        if (card.redMethod()) {
                            card.destroy();
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    break;
                }
            }
            if(playerScore > maxPlayerScore){
                System.out.println("MONSTER WON");
                //TODO End of battle
            }
        }
    }

    private void setUI(){
        playerScoreImage.currentScore.setText(playerScore);
        monsterScoreImage.currentScore.setText(monsterScore);
        playerScoreImage.maxScore.setText(maxPlayerScore);
        monsterScoreImage.maxScore.setText(maxMonsterScore);
        monsterMinInfo.setText(minMonsterScore);
        monsterTableCardInfo.setText(monsterTableCardCount);
        playerRollInfo.setText(game.mapScene.playerRoll);
        playerMoneyInfo.setText(game.mapScene.money);
        playerTableCardInfo.setText(game.mapScene.playerTableCardCount);
        playerHandCardInfo.setText(game.mapScene.playerHandCardCount);
    }

    public void createGame(){
        switch(MathUtils.random(1, 1)){
            case 1:
                monster = new Dog();
                break;
        }
        playerScore = 0;
        maxPlayerScore = game.mapScene.maxPlayerScore;
        monsterScore = 0;
        minMonsterScore = monster.getMin();
        maxMonsterScore = monster.getMax();
        monsterTableCardCount = monster.getTableCardCount();
        monsterTable = new HorizontalGroup();
        playerTable = new HorizontalGroup();
        playerHand = new HorizontalGroup();
        for(int i = 0; i < game.mapScene.playerTableCardCount && game.mapScene.playerDeck.size > 0; i++){
            playerTable.addActor(game.mapScene.playerDeck.removeIndex(0));
            playerScore += ((Card)playerTable.getChildren().get(playerTable.getChildren().size-1)).getStrength();
            if(game.mapScene.playerDeck.size == 0){
                while(game.mapScene.playerGraveyard.size > 0){
                    game.mapScene.playerDeck.add(game.mapScene.playerGraveyard.removeIndex(0));
                }
                game.mapScene.playerDeck.shuffle();
            }
        }
        for(int i = 0; i < game.mapScene.playerHandCardCount && game.mapScene.playerDeck.size > 0; i++){
            playerHand.addActor(game.mapScene.playerDeck.removeIndex(0));
            if(game.mapScene.playerDeck.size == 0){
                while(game.mapScene.playerGraveyard.size > 0){
                    game.mapScene.playerDeck.add(game.mapScene.playerGraveyard.removeIndex(0));
                }
                game.mapScene.playerDeck.shuffle();
            }
        }
        for(int i = 0; i < monsterTableCardCount; i++){
            monsterTable.addActor(monster.getCard());
            monsterScore += ((Card)monsterTable.getChildren().get(monsterTable.getChildren().size-1)).getStrength();
        }
        checkMonster();
    }

    public void resize(int width, int height) {
        if(width/ Gdx.graphics.getDensity() > 480f && height/Gdx.graphics.getDensity() > 800f) {
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
