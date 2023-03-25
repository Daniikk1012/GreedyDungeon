package com.wgsoft.game.gd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StreamUtils;
import com.wgsoft.game.gd.scenes.GameScene;
import com.wgsoft.game.gd.scenes.MainMenuScene;
import com.wgsoft.game.gd.scenes.MapScene;
import com.wgsoft.wgscene.Transition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class MyGdxGame extends Game {
    public static MyGdxGame game;

    public enum GraphicsQuality{
        LOW("low"),
        NORMAL("normal"),
        HIGH("high");
        private String name;
        GraphicsQuality(String name){
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    public GraphicsQuality quality;

    public Skin skin;

    public I18NBundle bundle;

    public MainMenuScene mainMenuScene;
    public MapScene mapScene;
    public GameScene gameScene;

    public MyGdxGame(){
        game = this;
    }

    @Override
    public void create() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);

        quality = GraphicsQuality.NORMAL; //TODO Remove

        loadSkin();

        createBundle("en"); //TODO Remove

        mainMenuScene = new MainMenuScene();
        mapScene = new MapScene();
        gameScene = new GameScene();

        setScreen(new Transition(this, null, mainMenuScene, true, 2f));
    }

    public void createBundle(String s1, String s2, String s3){
        bundle = I18NBundle.createBundle(Gdx.files.internal("bundles/bundle"), new Locale(s1, s2, s3));
    }

    public void createBundle(String s1, String s2){
        bundle = I18NBundle.createBundle(Gdx.files.internal("bundles/bundle"), new Locale(s1, s2));
    }

    public void createBundle(String s1){
        bundle = I18NBundle.createBundle(Gdx.files.internal("bundles/bundle"), new Locale(s1));
    }

    public void loadSkin(){
        if(skin != null){
            skin.dispose();
        }
        skin = new Skin();
        final int fontSize;
        BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal("graphics/"+quality+"/font/font.fnt").read()), 512);
        try {
            String line = reader.readLine();
            line = line.substring(line.indexOf("size=")+5);
            line = line.substring(0, line.indexOf(" "));
            fontSize = Integer.parseInt(line);
        }catch (Exception ex){
            throw new GdxRuntimeException("Error reading font file: "+ex);
        }finally {
            StreamUtils.closeQuietly(reader);
        }
        skin.add("small", new BitmapFont(Gdx.files.internal("graphics/"+quality+"/font/font.fnt")){{
            getData().setScale(32f/fontSize);
            Texture texture = null;
            Array.ArrayIterator<TextureRegion> regions = new Array.ArrayIterator<TextureRegion>(getRegions());
            for(TextureRegion region : regions){
                if(region.getTexture() != texture){
                    texture = region.getTexture();
                    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                }
            }
            setUseIntegerPositions(false);
        }}, BitmapFont.class);
        skin.add("medium", new BitmapFont(Gdx.files.internal("graphics/"+quality+"/font/font.fnt")){{
            getData().setScale(48f/fontSize);
            Texture texture = null;
            Array.ArrayIterator<TextureRegion> regions = new Array.ArrayIterator<TextureRegion>(getRegions());
            for(TextureRegion region : regions){
                if(region.getTexture() != texture){
                    texture = region.getTexture();
                    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                }
            }
            setUseIntegerPositions(false);
        }}, BitmapFont.class);
        skin.add("large", new BitmapFont(Gdx.files.internal("graphics/"+quality+"/font/font.fnt")){{
            getData().setScale(64f/fontSize);
            Texture texture = null;
            Array.ArrayIterator<TextureRegion> regions = new Array.ArrayIterator<TextureRegion>(getRegions());
            for(TextureRegion region : regions){
                if(region.getTexture() != texture){
                    texture = region.getTexture();
                    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                }
            }
            setUseIntegerPositions(false);
        }}, BitmapFont.class);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("graphics/"+quality+"/skin.atlas")));
        skin.load(Gdx.files.internal("graphics/skin.json"));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    @Override
    public void dispose() {
        skin.dispose();

        mainMenuScene.dispose();
        mapScene.dispose();
        gameScene.dispose();
    }
}
