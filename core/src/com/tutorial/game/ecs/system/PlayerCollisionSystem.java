package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.WorldContactListener;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.GameObjectComponent;
import com.tutorial.game.ecs.component.RemoveComponent;

public class PlayerCollisionSystem extends IteratingSystem implements WorldContactListener.PlayerCollisionListener {

    public PlayerCollisionSystem(final TutorialGame context) {
        super(Family.all(RemoveComponent.class).get());
        context.getWorldContactListener().addPlayerCollisionListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        getEngine().removeEntity(entity);
    }

    @Override
    public void playerCollision(Entity player, Entity gameObject) {
        final GameObjectComponent gameObjCpm = ECSEngine.gameObjCmpMapper.get(gameObject);

        switch (gameObjCpm.type) {
            case CRYSTAL:
                //remove crystal
                gameObject.add(((ECSEngine) getEngine()).createComponent(RemoveComponent.class));
                break;
            case AXE:
                ECSEngine.playerCmpMapper.get(player).hasAxe = true;
                gameObject.add(((ECSEngine) getEngine()).createComponent(RemoveComponent.class));
                break;
            case TREE:
                if (ECSEngine.playerCmpMapper.get(player).hasAxe){
                    gameObject.add(((ECSEngine) getEngine()).createComponent(RemoveComponent.class));
                }
                break;
            case FIRESTONE:
                break;
        }
    }
}
