package com.tutorial.game.view;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.ECS.ECSEngine;
import com.tutorial.game.ECS.component.AnimationComponent;
import com.tutorial.game.ECS.component.B2DComponent;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.map.Map;
import com.tutorial.game.map.MapListener;

import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class GameRenderer implements Disposable, MapListener {
    private final AssetManager assetManager;
    private final FitViewport viewport;
    private final OrthographicCamera gameCamera;
    private final SpriteBatch spriteBatch;

    private final ImmutableArray<Entity> animateEntity;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Array<TiledMapTileLayer> tiledMapLayers;

    private final GLProfiler profiler;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World world;

    private Sprite dummySprite;

    public GameRenderer(TutorialGame context) {
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getCamera();
        spriteBatch = context.getSpriteBatch();

        animateEntity = context.getEcsEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).get());

        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, spriteBatch);
        context.getMapManager().addMapListener(this);
        tiledMapLayers = new Array<TiledMapTileLayer>();

        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        if (profiler.isEnabled()){
            box2DDebugRenderer = new Box2DDebugRenderer();
            world = context.getWorld();
        }else {
            box2DDebugRenderer = null;
            world = null;
        }
    }

    public void render(final float alpha){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(false);
        spriteBatch.begin();
        if (mapRenderer.getMap() != null){
            AnimatedTiledMapTile.updateAnimationBaseTime();
            mapRenderer.setView(gameCamera);
            for (final TiledMapTileLayer layer: tiledMapLayers){
                mapRenderer.renderTileLayer(layer);
            }
        }

        for (final Entity entity: animateEntity){
            renderEntity(entity, alpha);
        }
        spriteBatch.end();

        if (profiler.isEnabled()){
            Gdx.app.debug("RenderInfo", "Bindings " + profiler.getTextureBindings());
            Gdx.app.debug("RenderInfo", "Drawcalls " + profiler.getDrawCalls());
            profiler.reset();

            box2DDebugRenderer.render(world, gameCamera.combined);
        }
    }

    private void renderEntity(Entity entity, float alpha) {
        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);

        b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(), alpha);
        dummySprite.setBounds(b2DComponent.renderPosition.x - b2DComponent.width/2, b2DComponent.renderPosition.y - b2DComponent.height/2, b2DComponent.width, b2DComponent.height);
        dummySprite.draw(spriteBatch);
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

        if (dummySprite == null){
            dummySprite = assetManager.get("characters_and_effects/character_and_effect.atlas", TextureAtlas.class).createSprite("fireball");
            dummySprite.setOriginCenter();
        }
    }
}
