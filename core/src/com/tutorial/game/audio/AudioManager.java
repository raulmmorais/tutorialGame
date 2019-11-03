package com.tutorial.game.audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.tutorial.game.TutorialGame;

public class AudioManager {
    private AudioType currentMusicType;
    private Music currentMusic;
    private final AssetManager assetManager;

    public AudioManager(TutorialGame context){
        assetManager = context.getAssetManager();
        currentMusicType = null;
        currentMusic = null;
    }

    public void playAudio(AudioType type){
        if (type.isMusic()){
            if (currentMusicType == type){
                return;//do nothing
            }else if (currentMusic != null){
                currentMusic.stop();
            }

            currentMusicType = type;
            currentMusic = assetManager.get(type.getFilepatch(), Music.class);
            currentMusic.setLooping(true);
            currentMusic.setVolume(type.getVolume());
            currentMusic.play();
        }else {
            assetManager.get(type.getFilepatch(), Sound.class).play(type.getVolume());
        }
    }

    public void stopCurrentMusic(){
        if (currentMusic != null){
            currentMusic.stop();
            currentMusic = null;
            currentMusicType = null;
        }
    }
}
