package com.tutorial.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.B2DComponent;
import com.tutorial.game.ecs.component.PlayerComponent;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeyInputListener;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;

import static com.tutorial.game.input.GameKeys.DOWN;
import static com.tutorial.game.input.GameKeys.LEFT;
import static com.tutorial.game.input.GameKeys.RIGHT;
import static com.tutorial.game.input.GameKeys.UP;

public class PlayerMovementSystem extends IteratingSystem implements GameKeyInputListener {
    private int xFactor;
    private int yFactor;

    public PlayerMovementSystem(final TutorialGame context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        context.getInputManager().addInputListener(this);
        xFactor = 0;
        yFactor = 0;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final PlayerComponent playerComponent = ECSEngine.playerCmpMapper.get(entity);
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);

        b2DComponent.body.applyLinearImpulse(
                (xFactor * playerComponent.speed.x - b2DComponent.body.getLinearVelocity().x) * b2DComponent.body.getMass(),
                (yFactor * playerComponent.speed.y - b2DComponent.body.getLinearVelocity().y) * b2DComponent.body.getMass(),
                b2DComponent.body.getWorldCenter().x, b2DComponent.body.getWorldCenter().y, true
        );
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        switch (key){
            case LEFT:
                xFactor = -1;
                break;
            case RIGHT:
                xFactor = 1;
                break;
            case UP:
                yFactor = 1;
                break;
            case DOWN:
                yFactor = -1;
                break;
            default:
                return;
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        switch (key){
            case LEFT:
                xFactor = manager.isKeyPressed(RIGHT) ? 1: 0;
                break;
            case RIGHT:
                xFactor = manager.isKeyPressed(LEFT) ? -1: 0;
                break;
            case UP:
                yFactor = manager.isKeyPressed(DOWN) ? -1: 0;
                break;
            case DOWN:
                yFactor = manager.isKeyPressed(UP) ? 1: 0;
                break;
            default:
                return;
        }
    }


}
