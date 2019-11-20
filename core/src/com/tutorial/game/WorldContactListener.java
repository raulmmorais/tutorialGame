package com.tutorial.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;


import static com.tutorial.game.TutorialGame.BIT_GAME_OBJECT;
import static com.tutorial.game.TutorialGame.BIT_PLAYER;

public class WorldContactListener implements ContactListener {
    private final Array<PlayerCollisionListener> listeners;

    public WorldContactListener() {
        listeners = new Array<>();
    }

    public void addPlayerCollisionListener(final PlayerCollisionListener listener){
        listeners.add(listener);
    }

    @Override
    public void beginContact(Contact contact) {
        final Entity player;
        final Entity gameObj;
        final Body bodyA = contact.getFixtureA().getBody();
        final Body bodyB = contact.getFixtureB().getBody();
        final short catFixA = contact.getFixtureA().getFilterData().categoryBits;
        final short catFixB = contact.getFixtureB().getFilterData().categoryBits;

        if ( (catFixA & BIT_PLAYER) == BIT_PLAYER ){
            player = (Entity) bodyA.getUserData();
        }else if ((catFixB & BIT_PLAYER) == BIT_PLAYER ){
            player = (Entity) bodyB.getUserData();
        }else {
            return;
        }
        if ( (catFixA & BIT_GAME_OBJECT) == BIT_GAME_OBJECT ){
            gameObj = (Entity) bodyA.getUserData();
        }else if ((catFixB & BIT_GAME_OBJECT) == BIT_GAME_OBJECT ){
            gameObj = (Entity) bodyB.getUserData();
        }else {
            return;
        }

        for (final PlayerCollisionListener listener: listeners){
            listener.playerCollision(player, gameObj);
        }

    }

    @Override
    public void endContact(Contact contact) {
        //not necessary
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public interface PlayerCollisionListener{
        void playerCollision(final Entity player, final Entity gameObject);
    }
}
