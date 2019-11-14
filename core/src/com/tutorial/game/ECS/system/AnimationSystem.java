package com.tutorial.game.ECS.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tutorial.game.ECS.ECSEngine;
import com.tutorial.game.ECS.component.AnimationComponent;
import com.tutorial.game.TutorialGame;

public class AnimationSystem extends IteratingSystem {
    public AnimationSystem(TutorialGame context) {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final AnimationComponent animationComponent = ECSEngine.aniCmpMapper.get(entity);
        if (animationComponent.aniType != null){
            animationComponent.aniTime += deltaTime;

        }
    }
}
