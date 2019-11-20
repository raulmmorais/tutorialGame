package com.tutorial.game.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.type.MapType;

import java.util.EnumMap;

import static com.tutorial.game.TutorialGame.BIT_GROUND;

public class MapManager {
    public static final String TAG = MapManager.class.getSimpleName();

    private Map currentMap;
    private MapType currentMapType;
    private final World world;
    private final Array<Body> bodies;

    private final AssetManager assetManager;
    private final ECSEngine ecsEngine;
    private final Array<Entity> gameObjectsToRemove;

    private final EnumMap<MapType, Map> mapCache;
    private final Array<MapListener> listeners;

    public MapManager(TutorialGame context){
        currentMapType = null;
        currentMap = null;
        world = context.getWorld();
        ecsEngine = context.getEcsEngine();
        assetManager = context.getAssetManager();
        gameObjectsToRemove = new Array<>();
        bodies = new Array<>();
        mapCache = new EnumMap<MapType, Map>(MapType.class);
        listeners = new Array<>();
    }

    public void addMapListener(final MapListener mapListener){
        listeners.add(mapListener);
    }

    public void setMap(final MapType type){
        if (currentMapType == type){
            return;//No map to set
        }
        if (currentMap != null){
            world.getBodies(bodies);
            destroyCollisionAreas();
            destroyGameObjects();
        }

        //set Map
        Gdx.app.debug(TAG, "Changing to map: " + type);
        currentMap = mapCache.get(type);

        if (currentMap == null){
            Gdx.app.debug(TAG, "Creating a map of type: " + type);
            final TiledMap tiledMap = assetManager.get(type.getFilePatch(), TiledMap.class);
            currentMap = new Map(tiledMap);
            mapCache.put(type, currentMap);
        }

        spawnCollisionAreas();
        spawnGameObjects();

        for (final MapListener listener: listeners){
            listener.mapChange(currentMap);
        }

    }

    private void destroyCollisionAreas() {
        for (final Body body: bodies){
            if ("GROUND".equals(body.getUserData())){
                world.destroyBody(body);
            }
        }
    }

    private void destroyGameObjects() {
        for (final Entity entity: ecsEngine.getEntities()){
            if (ECSEngine.gameObjCmpMapper.get(entity) != null){
                gameObjectsToRemove.add(entity);
            }
        }
        for(final Entity entity: gameObjectsToRemove){
            ecsEngine.removeEntity(entity);
        }
        gameObjectsToRemove.clear();
    }

    private void spawnCollisionAreas() {
        for (final CollisionArea collisionArea: currentMap.getCollisionAreas()){
            TutorialGame.resetBodyAndFixtureDefinition();
            TutorialGame.BODY_DEF.position.set(collisionArea.getX(), collisionArea.getY());
            TutorialGame.BODY_DEF.fixedRotation = true;
            final Body body = world.createBody(TutorialGame.BODY_DEF);
            body.setUserData("GROUND");

            TutorialGame.FIXTURE_DEF.filter.categoryBits = BIT_GROUND;
            TutorialGame.FIXTURE_DEF.filter.maskBits = -1;
            final ChainShape chainShape = new ChainShape();
            chainShape.createChain(collisionArea.getVertices());
            TutorialGame.FIXTURE_DEF.shape = chainShape;
            body.createFixture(TutorialGame.FIXTURE_DEF);
            chainShape.dispose();
        }

    }

    private void spawnGameObjects(){
        for (final GameObject gameObject: currentMap.getGameObjects()){
            ecsEngine.createGameObject(gameObject);
        }
    }

    public Map getCurrentMap() {
        return currentMap;
    }
}
