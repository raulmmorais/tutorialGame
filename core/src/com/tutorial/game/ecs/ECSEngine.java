package com.tutorial.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tutorial.game.ecs.component.AnimationComponent;
import com.tutorial.game.ecs.component.ParticleEffectComponent;
import com.tutorial.game.ecs.system.LightSystem;
import com.tutorial.game.ecs.system.ParticleEffectSystem;
import com.tutorial.game.ecs.system.PlayerCameraSystem;
import com.tutorial.game.ecs.system.PlayerCollisionSystem;
import com.tutorial.game.type.AnimationType;
import com.tutorial.game.ecs.component.B2DComponent;
import com.tutorial.game.ecs.component.GameObjectComponent;
import com.tutorial.game.ecs.component.PlayerComponent;
import com.tutorial.game.ecs.system.AnimationSystem;
import com.tutorial.game.ecs.system.PlayerAnimationSystem;
import com.tutorial.game.ecs.system.PlayerMovementSystem;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.map.GameObject;
import com.tutorial.game.type.ParticleEffectType;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import static com.tutorial.game.TutorialGame.BIT_GAME_OBJECT;
import static com.tutorial.game.TutorialGame.BIT_GROUND;
import static com.tutorial.game.TutorialGame.BIT_PLAYER;
import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class ECSEngine extends PooledEngine {
    private final static String TAG = ECSEngine.class.getSimpleName();

    public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCmpMapper = ComponentMapper.getFor(B2DComponent.class);
    public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
    public static final ComponentMapper<ParticleEffectComponent> peCmpMapper = ComponentMapper.getFor(ParticleEffectComponent.class);

    private final RayHandler rayHandler;
    private final World world;
    private Vector2 localPosition = new Vector2();
    private Vector2 posBeforeRotation = new Vector2();
    private Vector2 posAfterRotation = new Vector2();

    public ECSEngine(final TutorialGame context) {
        super();
        this.rayHandler = context.getRayHandler();
        this.world = context.getWorld();
        localPosition = new Vector2();
        posBeforeRotation = new Vector2();
        posAfterRotation = new Vector2();

        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
        this.addSystem(new PlayerAnimationSystem(context));
        this.addSystem(new LightSystem());
        this.addSystem(new ParticleEffectSystem(context));
        this.addSystem(new PlayerCollisionSystem(context));
    }

    public Entity createPlayer(final Vector2 playerLocation, float width, float height){
        final Entity player = this.createEntity();
        //playerComponent
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);

        //box 2d
        TutorialGame.resetBodyAndFixtureDefinition();
        final B2DComponent b2DCmp = this.createComponent(B2DComponent.class);
        TutorialGame.BODY_DEF.position.set(playerLocation.x, playerLocation.y + height + 0.5f);
        TutorialGame.BODY_DEF.fixedRotation = true;
        TutorialGame.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        b2DCmp.body = world.createBody(TutorialGame.BODY_DEF);
        b2DCmp.body.setUserData(player);
        b2DCmp.width = width;
        b2DCmp.height = height;
        b2DCmp.renderPosition.set(b2DCmp.body.getPosition());
        //configure fixture
        TutorialGame.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
        TutorialGame.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_GAME_OBJECT;
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(b2DCmp.width * 0.5f, b2DCmp.height * 0.5f);
        TutorialGame.FIXTURE_DEF.shape = pShape;
        b2DCmp.body.createFixture(TutorialGame.FIXTURE_DEF);
        pShape.dispose();

        //create a player light
        b2DCmp.lightDistance = 6;
        b2DCmp.lightFluctuationSpeed = 4;
        b2DCmp.light = new PointLight(rayHandler, 64, new Color(1, 1, 1, 0.7f), b2DCmp.lightDistance, b2DCmp.body.getPosition().x, b2DCmp.body.getPosition().y);
        //b2DCmp.light = new ConeLight(rayHandler, 64, new Color(1, 1, 1, 0.7f), b2DCmp.lightDistance, b2DCmp.body.getPosition().x, b2DCmp.body.getPosition().y, b2DCmp.body.getAngle(), 45);
        b2DCmp.lightFluctuationDistance = b2DCmp.light.getDistance() * 0.16f;
        b2DCmp.light.attachToBody(b2DCmp.body);

        player.add(b2DCmp);

        //Animations
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        animationComponent.aniType = AnimationType.HERO_MOVE_DOWN;
        animationComponent.width = 64 * UNIT_SCALE * width;
        animationComponent.height = 64 * UNIT_SCALE * height;
        player.add(animationComponent);

        addEntity(player);
        return player;
    }

    public void createGameObject(final GameObject gameObject){
        final Entity gameObjectEntity = this.createEntity();

        final GameObjectComponent gameObjectComponent = this.createComponent(GameObjectComponent.class);
        gameObjectComponent.animationIndex = gameObject.getAnimationIndex();
        gameObjectComponent.type = gameObject.getType();
        gameObjectEntity.add(gameObjectComponent);

        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        animationComponent.aniType = null;
        animationComponent.width = gameObject.getWidth();
        animationComponent.height = gameObject.getHeight();
        gameObjectEntity.add(animationComponent);

        TutorialGame.resetBodyAndFixtureDefinition();
        final float halfw = gameObject.getWidth() * 0.5f;
        final float halfh = gameObject.getHeight() * 0.5f;
        final float angleRad = -gameObject.getRotDegree() * MathUtils.degreesToRadians;
        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        TutorialGame.BODY_DEF.type = BodyDef.BodyType.StaticBody;
        TutorialGame.BODY_DEF.position.set(gameObject.getPosition().x + halfw, gameObject.getPosition().y + halfh);
        b2DComponent.body = world.createBody(TutorialGame.BODY_DEF);
        b2DComponent.body.setUserData(gameObjectEntity);
        b2DComponent.width = gameObject.getWidth();
        b2DComponent.height = gameObject.getHeight();

        //save position before rotation
        localPosition.set(-halfw, -halfh);
        posBeforeRotation.set(b2DComponent.body.getWorldPoint(localPosition));
        //rotate body
        b2DComponent.body.setTransform(b2DComponent.body.getPosition(), angleRad);
        //get position after rotating
        posAfterRotation.set(b2DComponent.body.getWorldPoint(localPosition));
        b2DComponent.body.setTransform(b2DComponent.body.getPosition().add(posBeforeRotation).sub(posAfterRotation), angleRad);
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition().x - animationComponent.width * 0.5f, b2DComponent.body.getPosition().y - animationComponent.height * 0.5f);

        TutorialGame.FIXTURE_DEF.filter.categoryBits = BIT_GAME_OBJECT;
        TutorialGame.FIXTURE_DEF.filter.maskBits = BIT_PLAYER;
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfw, halfh);
        TutorialGame.FIXTURE_DEF.shape = shape;
        b2DComponent.body.createFixture(TutorialGame.FIXTURE_DEF);
        shape.dispose();
        gameObjectEntity.add(b2DComponent);

        //particle system
        final ParticleEffectComponent peCpm = createComponent(ParticleEffectComponent.class);
        peCpm.effectType = ParticleEffectType.TORCH;
        peCpm.scaling = 0.3f;
        peCpm.effectPosition.set(b2DComponent.body.getPosition());
        gameObjectEntity.add(peCpm);

        this.addEntity(gameObjectEntity);
    }
}