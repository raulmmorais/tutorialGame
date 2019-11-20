package com.tutorial.game.type;

public enum ParticleEffectType {
    TORCH("characters_and_effects/torch.p");

    private final String effectFilePath;
    ParticleEffectType(String effectFilePath) {
        this.effectFilePath = effectFilePath;
    }

    public String getEffectFilePath() {
        return effectFilePath;
    }
}
