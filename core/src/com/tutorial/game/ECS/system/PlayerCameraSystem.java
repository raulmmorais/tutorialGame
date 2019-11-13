package com.tutorial.game.ECS.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.tutorial.game.ECS.ECSEngine;
import com.tutorial.game.ECS.component.B2DComponent;
import com.tutorial.game.ECS.component.PlayerComponent;
import com.tutorial.game.TutorialGame;

public class PlayerCameraSystem extends IteratingSystem {
    final OrthographicCamera camera;
    public PlayerCameraSystem(TutorialGame context){
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        camera = context.getCamera();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        camera.position.set(ECSEngine.b2dCmpMapper.get(entity).renderPosition, 0);
        camera.update();
    }
}
