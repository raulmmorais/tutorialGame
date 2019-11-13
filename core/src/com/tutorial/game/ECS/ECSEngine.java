package com.tutorial.game.ECS;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tutorial.game.ECS.component.AnimationComponent;
import com.tutorial.game.ECS.component.B2DComponent;
import com.tutorial.game.ECS.component.PlayerComponent;
import com.tutorial.game.ECS.system.PlayerCameraSystem;
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

    public ECSEngine(final TutorialGame context) {
        super();
        this.context = context;
        this.world = context.getWorld();

        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
    }

    public void createPlayer(final Vector2 playerLocation, float width, float height){
        final Entity player = this.createEntity();
        //playerComponent
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);

        //box 2d
        TutorialGame.resetBodiesAndFixture();
        final B2DComponent b2DCmp = this.createComponent(B2DComponent.class);
        TutorialGame.BODY_DEF.position.set(playerLocation.x, playerLocation.y + height + 0.5f);
        TutorialGame.BODY_DEF.fixedRotation = true;
        TutorialGame.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        b2DCmp.body = world.createBody(TutorialGame.BODY_DEF);
        b2DCmp.body.setUserData("Player");
        b2DCmp.width = width;
        b2DCmp.height = height;
        b2DCmp.renderPosition.set(b2DCmp.body.getPosition());
        //configure fixture
        TutorialGame.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
        TutorialGame.FIXTURE_DEF.filter.maskBits = BIT_GROUND;
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(b2DCmp.width * 0.5f, b2DCmp.height * 0.5f);
        TutorialGame.FIXTURE_DEF.shape = pShape;
        b2DCmp.body.createFixture(TutorialGame.FIXTURE_DEF);
        pShape.dispose();
        player.add(b2DCmp);

        //Animations
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        player.add(animationComponent);

        addEntity(player);
    }
}