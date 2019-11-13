package com.tutorial.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.audio.AudioType;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.map.MapType;
import com.tutorial.game.view.LoadingUI;

public class LoadingScreen extends AbstractScreen<LoadingUI> {
    private final AssetManager assetManager;
    private boolean isMusicLoaded;

    public LoadingScreen(final TutorialGame context) {
        super(context);

        assetManager = context.getAssetManager();

        //load characters and effects
        assetManager.load("characters_and_effects/character_and_effect.atlas", TextureAtlas.class);

        //loading Maps
        for (MapType mapType : MapType.values()){
            assetManager.load(mapType.getFilePatch(), TiledMap.class);
        }
        
        //loading sounds
        isMusicLoaded = false;
        for(final AudioType audioType: AudioType.values()){
            assetManager.load(audioType.getFilepatch(), audioType.isMusic() ? Music.class: Sound.class);
        }
        viewport.apply();
    }

    @Override
    protected LoadingUI getScreenUI(TutorialGame context) {
        return new LoadingUI(context);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.2f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        assetManager.update();

        if (!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilepatch())){
            isMusicLoaded = true;
            audioManager.playAudio(AudioType.INTRO);
        }

        screenUI.setProgress(assetManager.getProgress());

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        if (assetManager.getProgress() >= 1){
            audioManager.playAudio(AudioType.SELECT);
            context.setScreen(ScreenType.GAME);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }

    @Override
    public void hide() {
        super.hide();
        audioManager.stopCurrentMusic();
    }
}
