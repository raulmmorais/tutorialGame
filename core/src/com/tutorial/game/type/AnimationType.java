package com.tutorial.game.type;

public enum AnimationType {
    HERO_MOVE_UP("test/characters_and_effects.atlas", "hero2", 0.05f, 0),
    HERO_MOVE_DOWN("test/characters_and_effects.atlas", "hero2", 0.05f, 2),
    HERO_MOVE_LEFT("test/characters_and_effects.atlas", "hero2", 0.05f, 1),
    HERO_MOVE_RIGHT("test/characters_and_effects.atlas", "hero2", 0.05f, 3);

    private final String atlasPath;
    private final String atlasKey;
    private final float frameTime;
    private final int rowIndex;

    AnimationType(String atlasPath, String atlasKey, float frameTime, int rowIndex){
        this.atlasPath = atlasPath;
        this.atlasKey = atlasKey;
        this.frameTime = frameTime;
        this.rowIndex = rowIndex;
    }

    public String getAtlasPath() {
        return atlasPath;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public float getFrameTime() {
        return frameTime;
    }

    public int getRowIndex() {
        return rowIndex;
    }
}
