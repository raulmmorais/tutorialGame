package com.tutorial.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.TutorialGame;

import java.util.EnumMap;

import static com.tutorial.game.TutorialGame.BIT_GROUND;

public class MapManager {
    public static final String TAG = MapManager.class.getSimpleName();

    private Map currentMap;
    private MapType currentMapType;
    private final World world;
    private final Array<Body> bodies;

    private final AssetManager assetManager;

    private final EnumMap<MapType, Map> mapCache;
    private final Array<MapListener> listeners;

    public MapManager(TutorialGame context){
        currentMapType = null;
        currentMap = null;
        world = context.getWorld();
        assetManager = context.getAssetManager();
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

    private void spawnCollisionAreas() {
        for (final CollisionArea collisionArea: currentMap.getCollisionAreas()){
            TutorialGame.resetBodiesAndFixture();
            TutorialGame.BODY_DEF.position.set(collisionArea.getX(), collisionArea.getY());
            TutorialGame.BODY_DEF.gravityScale = 1;
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

    public Map getCurrentMap() {
        return currentMap;
    }
}
