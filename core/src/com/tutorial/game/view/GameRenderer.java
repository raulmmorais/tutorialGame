package com.tutorial.game.view;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.AnimationComponent;
import com.tutorial.game.ecs.component.ParticleEffectComponent;
import com.tutorial.game.type.AnimationType;
import com.tutorial.game.ecs.component.B2DComponent;
import com.tutorial.game.ecs.component.GameObjectComponent;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.map.Map;
import com.tutorial.game.map.MapListener;

import java.util.EnumMap;

import box2dLight.RayHandler;

import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class GameRenderer implements Disposable, MapListener {
    private static final String TAG = GameRenderer.class.getSimpleName();

    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;
    private final ObjectMap<String, TextureRegion[][]> regionCache;
    private final EnumMap<AnimationType, Animation<Sprite>> animationCache;

    private final ImmutableArray<Entity> gameObjectEntities;
    private final ImmutableArray<Entity> animatedEntities;
    private final ImmutableArray<Entity> effectsToRender;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private  IntMap<Animation<Sprite>> mapAnimations;
    private final Array<TiledMapTileLayer> tiledMapLayers;

    private final GLProfiler profiler;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World world;
    private final RayHandler rayHandler;


    public GameRenderer(TutorialGame context) {
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getGameCamera();
        spriteBatch = context.getSpriteBatch();

        animationCache = new EnumMap<AnimationType, Animation<Sprite>>(AnimationType.class);
        regionCache = new ObjectMap<>();

        gameObjectEntities = context.getEcsEngine().getEntitiesFor(Family.all(GameObjectComponent.class, B2DComponent.class, AnimationComponent.class).get());
        animatedEntities = context.getEcsEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).exclude(GameObjectComponent.class).get());
        effectsToRender = context.getEcsEngine().getEntitiesFor(Family.all(ParticleEffectComponent.class).get());

        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, spriteBatch);
        context.getMapManager().addMapListener(this);
        tiledMapLayers = new Array<TiledMapTileLayer>();

        profiler = new GLProfiler(Gdx.graphics);
        profiler.disable();
        if (profiler.isEnabled()){
            box2DDebugRenderer = new Box2DDebugRenderer();
            world = context.getWorld();
        }else {
            box2DDebugRenderer = null;
            world = null;
        }
        rayHandler = context.getRayHandler();
    }

    public void render(final float alpha){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(false);

        mapRenderer.setView(gameCamera);
        spriteBatch.begin();
        if (mapRenderer.getMap() != null){
            AnimatedTiledMapTile.updateAnimationBaseTime();
            for (final TiledMapTileLayer layer: tiledMapLayers){
                mapRenderer.renderTileLayer(layer);
            }
        }
        for (final Entity entity: gameObjectEntities){
            renderGameObject(entity, alpha);
        }
        for (final Entity entity: animatedEntities){
            renderEntity(entity, alpha);
        }
        for (final Entity entity: effectsToRender){
            final ParticleEffectComponent peCmp = ECSEngine.peCmpMapper.get(entity);
            if (peCmp.effect != null){
                peCmp.effect.draw(spriteBatch);
            }
        }
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.end();

        rayHandler.setCombinedMatrix(gameCamera);
        rayHandler.updateAndRender();

        if (profiler.isEnabled()){
            profiler.reset();
            box2DDebugRenderer.render(world, gameCamera.combined);
        }
    }

    private void renderGameObject(Entity entity, float alpha) {
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);
        final AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
        final GameObjectComponent gameObjectComponent = ECSEngine.gameObjCmpMapper.get(entity);

        if (gameObjectComponent.animationIndex != -1){
            final Animation<Sprite> animation = mapAnimations.get(gameObjectComponent.animationIndex);
            final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);

            frame.setBounds(b2DComponent.renderPosition.x, b2DComponent.renderPosition.y, aniComponent.width, aniComponent.height);
            frame.setOriginCenter();
            frame.setRotation(b2DComponent.body.getAngle() * MathUtils.radDeg);
            frame.draw(spriteBatch);
        }
    }

    private void renderEntity(Entity entity, float alpha) {
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);
        final AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);

        if (aniComponent.aniType != null){
            final Animation<Sprite> animation = getAnimation(aniComponent.aniType);
            final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);
            b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(), alpha);
            frame.setBounds(b2DComponent.renderPosition.x - aniComponent.width * 0.5f, b2DComponent.renderPosition.y - aniComponent.height * 0.5f, aniComponent.width, aniComponent.height);
            frame.draw(spriteBatch);
        }
    }

    private Animation<Sprite> getAnimation(AnimationType aniType) {
        Animation<Sprite> animation = animationCache.get(aniType);
        if (animation == null){
            Gdx.app.debug(TAG, "Creating animation of type: " + aniType);

            TextureRegion[][] textureRegions = regionCache.get(aniType.getAtlasKey());
            if (textureRegions == null){
                final TextureAtlas.AtlasRegion atlasRegion = assetManager.get(aniType.getAtlasPath(), TextureAtlas.class).findRegion(aniType.getAtlasKey());
                textureRegions = atlasRegion.split(64, 64);
                regionCache.put(aniType.getAtlasKey(), textureRegions);
            }

            animation = new Animation<Sprite>(aniType.getFrameTime(), getKeyFrames(textureRegions[aniType.getRowIndex()]));
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animationCache.put(aniType, animation);
        }
        return animation;
    }

    private Sprite[] getKeyFrames(TextureRegion[] textureRegion) {
        final Sprite[] keyFrame = new Sprite[textureRegion.length];
        int i=0;
        for (TextureRegion region: textureRegion){
            final Sprite sprite = new Sprite(region);
            sprite.setOriginCenter();
            keyFrame[i++] = sprite;
        }
        return keyFrame;
    }

    @Override
    public void dispose() {
        if (box2DDebugRenderer != null)
            box2DDebugRenderer.dispose();
        mapRenderer.dispose();
    }

    @Override
    public void mapChange(final Map map) {
        mapRenderer.setMap(map.getTiledMap());
        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledMapLayers);
        mapAnimations = map.getMapAnimations();
    }
}
