package com.tutorial.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.tutorial.game.type.GameObjectType;

import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class Map {
    public static final String TAG = Map.class.getSimpleName();

    private final TiledMap tiledMap;
    private final Array<CollisionArea> collisionAreas;
    private final Vector2 startLocation;
    private final Array<GameObject> gameObjects;
    private IntMap<Animation<Sprite>> mapAnimations;

    public Map(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        collisionAreas = new Array<CollisionArea>();
        parseCollisionLayer();
        startLocation = new Vector2();
        parsePlayerStartLocation();

        gameObjects = new Array<>();
        mapAnimations = new IntMap<>();
        parseGameObjectLayer();
    }

    private void parseGameObjectLayer() {
        final MapLayer objectLayer = tiledMap.getLayers().get("gameObjects");
        if (objectLayer == null){
            Gdx.app.debug(TAG, "There is no gameObjects to render");
            return;
        }
        final MapObjects gameObjects = objectLayer.getObjects();
        for (MapObject gameObject:gameObjects ) {
            if (!(gameObject instanceof TiledMapTileMapObject)){
                Gdx.app.debug(TAG, "Game Object " + gameObject + " is not supported");
                continue;
            }

            final TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) gameObject;
            final MapProperties tiledMapObjProperties = tiledMapObj.getProperties();
            final MapProperties tileProperties = tiledMapObj.getTile().getProperties();
            final GameObjectType gameObjectType;
            if (tiledMapObjProperties.containsKey("type")){
                gameObjectType = GameObjectType.valueOf(tiledMapObjProperties.get("type", String.class));
            }else if (tileProperties.containsKey("type")){
                gameObjectType = GameObjectType.valueOf(tileProperties.get("type", String.class));
            }else{
                Gdx.app.log(TAG, "There is no game object defined for " + tiledMapObjProperties.get("id", Integer.class));
                continue;
            }
            final int animationIndex = tiledMapObj.getTile().getId();
            if (!createAnimation(animationIndex, tiledMapObj.getTile())){
                Gdx.app.debug(TAG, "Coud not create animation for tile " + tiledMapObjProperties.get("id", Integer.class));
                continue;
            }
            final float width = tiledMapObjProperties.get("width", Float.class) * UNIT_SCALE;
            final float height = tiledMapObjProperties.get("height", Float.class) * UNIT_SCALE;
            final float rotDegree = tiledMapObj.getRotation();
            final Vector2 position = new Vector2(tiledMapObj.getX() * UNIT_SCALE, tiledMapObj.getY() * UNIT_SCALE);
            this.gameObjects.add(new GameObject(gameObjectType, position, width, height, rotDegree, animationIndex));
        }
    }

    private boolean createAnimation(final int animationIndex, final TiledMapTile tile) {
        Animation<Sprite> animation = mapAnimations.get(animationIndex);
        if (animation == null){
            Gdx.app.debug(TAG, "Creating a new animation for tile " + tile.getId());
            if (tile instanceof AnimatedTiledMapTile){
                final AnimatedTiledMapTile animTiled = (AnimatedTiledMapTile) tile;
                final Sprite[] keyFrames = new Sprite[animTiled.getFrameTiles().length];
                int i = 0;
                for (final StaticTiledMapTile staticTile: animTiled.getFrameTiles()){
                    keyFrames[i++] = new Sprite(staticTile.getTextureRegion());
                }
                animation = new Animation<Sprite>(animTiled.getAnimationIntervals()[0] * 0.001f, keyFrames);
                mapAnimations.put(animationIndex, animation);
            }else if (tile instanceof StaticTiledMapTile){
                animation = new Animation<Sprite>(0, new Sprite(tile.getTextureRegion()));
                mapAnimations.put(animationIndex, animation);
            }else {
                Gdx.app.debug(TAG, "Tile of type " + tile + " is not supported for map animations");
                return false;
            }
        }
        return true;
    }

    private void parsePlayerStartLocation(){
        final MapLayer startLocationLayer = tiledMap.getLayers().get("playerStartLocation");
        if (startLocationLayer == null){
            Gdx.app.debug(TAG, "Where is the player layer??");
            return;
        }
        final MapObjects objects = startLocationLayer.getObjects();
        for (MapObject mapObj: objects){
            if (mapObj instanceof RectangleMapObject){
                RectangleMapObject rectangleMapObject = (RectangleMapObject)mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                startLocation.set((rectangle.x * UNIT_SCALE), (rectangle.y * UNIT_SCALE));
            }else {
                Gdx.app.debug(TAG, "MapObject of type " + mapObj + " is not supported for playerStartLocation layer!");
            }
        }
    }

    private void parseCollisionLayer() {
        final MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        if (collisionLayer == null){
            Gdx.app.debug(TAG, "Where is the collision layer??");
            return;
        }
        final MapObjects mapObjects = collisionLayer.getObjects();
        for (final MapObject mapOb : mapObjects) {
            if (mapOb instanceof RectangleMapObject){
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapOb;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                final float[] rectVertices = new float[10];
                //left-botton
                rectVertices[0] = 0;
                rectVertices[1] = 0;
                //left-top
                rectVertices[2] = 0;
                rectVertices[3] = rectangle.height;
                //right-top
                rectVertices[4] = rectangle.height;
                rectVertices[5] = rectangle.width;
                //right-botton
                rectVertices[6] = rectangle.width;
                rectVertices[7] = 0;
                //left-botton
                rectVertices[8] = 0;
                rectVertices[9] = 0;
                collisionAreas.add(new CollisionArea(rectangle.x, rectangle.y, rectVertices));
            }else if (mapOb instanceof PolylineMapObject){
                PolylineMapObject polylineMapObject = (PolylineMapObject) mapOb;
                final Polyline polyline = polylineMapObject.getPolyline();
                collisionAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));
            }else {
                Gdx.app.debug(TAG, "map object type: " + mapOb + " is not supported.");
            }
        }
    }

    public Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public IntMap<Animation<Sprite>> getMapAnimations() {
        return mapAnimations;
    }
}
