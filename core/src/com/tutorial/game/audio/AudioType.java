package com.tutorial.game.audio;

public enum AudioType {
    INTRO("audio/intro.mp3", true, 0.3f),
    SELECT("audio/select.wav", false, 0.5f);

    private final String filepatch;
    private final boolean isMusic;
    private final float volume;

    AudioType(String filapatch, boolean isMusic, float volume) {
        this.filepatch = filapatch;
        this.isMusic = isMusic;
        this.volume = volume;
    }

    public String getFilepatch() {
        return filepatch;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMusic() {
        return isMusic;
    }
}
