package com.tutorial.game.ECS;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tutorial.game.ECS.component.B2DComponent;
import com.tutorial.game.ECS.component.PlayerComponent;
import com.tutorial.game.ECS.system.PlayerMovementSystem;
import com.tutorial.game.TutorialGame;

import static com.tutorial.game.TutorialGame.BIT_GROUND;
import static com.tutorial.game.TutorialGame.BIT_PLAYER;

public class ECSEngine extends PooledEngine {
    private final static String TAG = ECSEngine.class.getSimpleName();

    public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCmpMapper = ComponentMapper.getFor(B2DComponent.class);

    private final TutorialGame context;
    private final World world;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final TutorialGame context) {
        super();
        this.context = context;
        this.world = context.getWorld();
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();

        this.addSystem(new PlayerMovementSystem(context));
    }

    public void createPlayer(final Vector2 playerSpawnLocation){
        final Entity player = this.createEntity();

        final B2DComponent b2DCmp = this.createComponent(B2DComponent.class);
        b2DCmp.width = 0.5f;
        b2DCmp.height = 0.5f;
        // body
        bodyDef.gravityScale = 1;
        bodyDef.position.set(playerSpawnLocation);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = false;
        b2DCmp.body = world.createBody(bodyDef);
        b2DCmp.body.setUserData("Player");
        // fixtures
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(b2DCmp.width * 0.5f, b2DCmp.height * 0.5f);
        fixtureDef.isSensor = false;
        fixtureDef.shape = pShape;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_GROUND;
        b2DCmp.body.createFixture(fixtureDef);
        pShape.dispose();
        player.add(b2DCmp);
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        player.add(playerComponent);

        addEntity(player);
    }
}