package com.tutorial.game.ECS.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.tutorial.game.ECS.ECSEngine;
import com.tutorial.game.ECS.component.B2DComponent;
import com.tutorial.game.ECS.component.PlayerComponent;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeyInputListener;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;

import static com.tutorial.game.input.GameKeys.DOWN;
import static com.tutorial.game.input.GameKeys.LEFT;
import static com.tutorial.game.input.GameKeys.RIGHT;
import static com.tutorial.game.input.GameKeys.UP;

public class PlayerMovementSystem extends IteratingSystem implements GameKeyInputListener {
    private boolean directionChange;
    private int xFactor;
    private int yFactor;

    public PlayerMovementSystem(final TutorialGame context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        context.getInputManager().addInputListener(this);
        directionChange = false;
        xFactor = 0;
        yFactor = 0;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (directionChange) {
            directionChange = false;
            final PlayerComponent playerCmp = ECSEngine.playerCmpMapper.get(entity);
            final B2DComponent b2DCmp = ECSEngine.b2dCmpMapper.get(entity);

            b2DCmp.body.applyLinearImpulse(
                    (xFactor * playerCmp.speed.x - b2DCmp.body.getLinearVelocity().x) * b2DCmp.body.getMass(),
                    (yFactor * playerCmp.speed.y - b2DCmp.body.getLinearVelocity().y) * b2DCmp.body.getMass(),
                    b2DCmp.body.getWorldCenter().x, b2DCmp.body.getWorldCenter().y, true
            );
            Gdx.app.debug("PlayerInfo", "Position x: " + b2DCmp.body.getPosition().x + "y:"+ b2DCmp.body.getPosition().y);
        }
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        switch (key){
            case LEFT:
                directionChange = true;
                xFactor = -1;
                break;
            case RIGHT:
                directionChange = true;
                xFactor = 1;
                break;
            case UP:
                directionChange = true;
                yFactor = 1;
                break;
            case DOWN:
                directionChange = true;
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
                directionChange = true;
                xFactor = manager.isKeyPressed(RIGHT) ? 1: 0;
                break;
            case RIGHT:
                directionChange = true;
                xFactor = manager.isKeyPressed(LEFT) ? -1: 0;
                break;
            case UP:
                directionChange = true;
                yFactor = manager.isKeyPressed(DOWN) ? -1: 0;
                break;
            case DOWN:
                directionChange = true;
                yFactor = manager.isKeyPressed(UP) ? 1: 0;
                break;
            default:
                return;
        }
    }
}
