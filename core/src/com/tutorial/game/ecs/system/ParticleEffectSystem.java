package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.ParticleEffectComponent;
import com.tutorial.game.type.ParticleEffectType;

import java.util.EnumMap;

public class ParticleEffectSystem extends IteratingSystem {
    private final EnumMap<ParticleEffectType, ParticleEffectPool> effectPools;
    private final AssetManager assetManager;

    public ParticleEffectSystem(final TutorialGame context) {
        super(Family.all(ParticleEffectComponent.class).get());
        assetManager = context.getAssetManager();
        effectPools = new EnumMap<ParticleEffectType, ParticleEffectPool>(ParticleEffectType.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final ParticleEffectComponent peCmp = ECSEngine.peCmpMapper.get(entity);

        if (peCmp.effect != null) {
            // update it
            peCmp.effect.update(deltaTime);
            if (peCmp.effect.isComplete()) {
                entity.remove(ParticleEffectComponent.class);
            }
        } else if (peCmp.effectType != null) {
            // create the effect
            ParticleEffectPool effectPool = effectPools.get(peCmp.effectType);
            if (effectPool == null) {
                final ParticleEffect particleEffect = assetManager.get(peCmp.effectType.getEffectFilePath(), ParticleEffect.class);
                particleEffect.setEmittersCleanUpBlendFunction(false);
                effectPool = new ParticleEffectPool(particleEffect, 1, 128);
                effectPools.put(peCmp.effectType, effectPool);
            }

            peCmp.effect = effectPool.obtain();
            peCmp.effect.setPosition(peCmp.effectPosition.x, peCmp.effectPosition.y);
            peCmp.effect.scaleEffect(peCmp.scaling);
        }
    }
}
