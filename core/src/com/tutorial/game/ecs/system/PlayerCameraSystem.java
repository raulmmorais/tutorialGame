package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.B2DComponent;
import com.tutorial.game.ecs.component.PlayerComponent;
import com.tutorial.game.TutorialGame;

public class PlayerCameraSystem extends IteratingSystem {
    final OrthographicCamera gameCamera;

    public PlayerCameraSystem(TutorialGame context){
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        gameCamera = context.getGameCamera();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        gameCamera.position.set(ECSEngine.b2dCmpMapper.get(entity).renderPosition, 0);
        //gameCamera.update();
    }
}
