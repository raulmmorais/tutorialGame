package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.AnimationComponent;
import com.tutorial.game.type.AnimationType;
import com.tutorial.game.ecs.component.B2DComponent;
import com.tutorial.game.ecs.component.PlayerComponent;
import com.tutorial.game.TutorialGame;

public class PlayerAnimationSystem extends IteratingSystem {
    public PlayerAnimationSystem(TutorialGame context) {
        super(Family.all(AnimationComponent.class, PlayerComponent.class, B2DComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final AnimationComponent animationComponent = ECSEngine.aniCmpMapper.get(entity);
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);

        if (b2DComponent.body.getLinearVelocity().equals(Vector2.Zero)){
            animationComponent.aniTime = 0;
        }else if (b2DComponent.body.getLinearVelocity().x > 0){
            animationComponent.aniType = AnimationType.HERO_MOVE_RIGHT;
        }else if (b2DComponent.body.getLinearVelocity().x < 0){
            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;
        }else if (b2DComponent.body.getLinearVelocity().y > 0){
            animationComponent.aniType = AnimationType.HERO_MOVE_UP;
        }else if (b2DComponent.body.getLinearVelocity().y < 0){
            animationComponent.aniType = AnimationType.HERO_MOVE_DOWN;
        }
    }
}
