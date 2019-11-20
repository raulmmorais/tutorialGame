package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.B2DComponent;

public class LightSystem extends IteratingSystem {
    public LightSystem() {
        super(Family.all(B2DComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);
        if (b2DComponent.light != null && b2DComponent.lightFluctuationDistance > 0){
            b2DComponent.lightFluctuationTime += (b2DComponent.lightFluctuationSpeed * deltaTime);
            if (b2DComponent.lightFluctuationTime > MathUtils.PI2){
                b2DComponent.lightFluctuationTime = 0;
            }
            b2DComponent.light.setDistance(b2DComponent.lightDistance + MathUtils.sin(b2DComponent.lightFluctuationTime) * b2DComponent.lightFluctuationDistance);
        }
    }
}
