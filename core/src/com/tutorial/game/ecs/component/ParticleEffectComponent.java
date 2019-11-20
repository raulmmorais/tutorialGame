package com.tutorial.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.tutorial.game.type.ParticleEffectType;

public class ParticleEffectComponent implements Component, Pool.Poolable {
    public ParticleEffectPool.PooledEffect effect;
    public ParticleEffectType effectType;
    public final Vector2 effectPosition = new Vector2();
    public float scaling;

    @Override
    public void reset() {
        if (effect != null){
            effect.free();
            effect = null;
        }
        effectPosition.set(0, 0);
        effectType = null;
        scaling = 0;
    }
}
