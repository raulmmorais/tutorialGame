package com.tutorial.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.map.CollisionArea;
import com.tutorial.game.map.Map;
import com.tutorial.game.ui.GameUI;

import static com.tutorial.game.TutorialGame.BIT_GROUND;
import static com.tutorial.game.TutorialGame.BIT_PLAYER;
import static com.tutorial.game.TutorialGame.UNIT_SCALE;
import static com.tutorial.game.input.GameKeys.DOWN;
import static com.tutorial.game.input.GameKeys.LEFT;
import static com.tutorial.game.input.GameKeys.RIGHT;
import static com.tutorial.game.input.GameKeys.UP;

public class GameScreen extends AbstractScreen {
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;
    private final AssetManager assetManager;
    private final Map map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera gameCamera;
    private final GLProfiler profiler;

    private Body player;

    private boolean directionChange;
    private int xFactor;
    private int yFactor;

    public GameScreen(TutorialGame context) {
        super(context);
        assetManager = context.getAssetManager();
        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, context.getSpriteBatch());
        this.gameCamera = context.getCamera();
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        this.bodyDef = new BodyDef();
        this.fixtureDef = new FixtureDef();
        final TiledMap tiledMap = assetManager.get("map/map.tmx", TiledMap.class);
        mapRenderer.setMap(tiledMap);
        map = new Map(tiledMap);
        spawnCollisionAreas();
        spawnPlayer();
    }

    @Override
    protected Table getScreenUI(TutorialGame context) {
        return new GameUI(context);
    }

    private void spawnPlayer(){
        //create a player
        resetBodieAndFixture();
        bodyDef.position.set(map.getStartLocator().x, map.getStartLocator().y + 0.5f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player = world.createBody(bodyDef);
        player.setUserData("Player");

        fixtureDef.density = 1;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_GROUND;
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(0.5f, 0.5f);
        fixtureDef.shape = pShape;
        player.createFixture(fixtureDef);
        pShape.dispose();
    }

    private void resetBodieAndFixture(){
        bodyDef.position.set(0, 0);
        bodyDef.gravityScale = 1;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = false;

        fixtureDef.density = 0;
        fixtureDef.isSensor = false;
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0.2f;
        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = -1;
        fixtureDef.shape = null;
    }

    private void spawnCollisionAreas() {
        for (final CollisionArea collisionArea: map.getCollisionAreas()){
            resetBodieAndFixture();
            bodyDef.position.set(collisionArea.getX(), collisionArea.getY());
            bodyDef.gravityScale = 1;
            bodyDef.fixedRotation = true;
            final Body body = world.createBody(bodyDef);
            body.setUserData("GROUND");

            fixtureDef.filter.categoryBits = BIT_GROUND;
            fixtureDef.filter.maskBits = -1;
            final ChainShape chainShape = new ChainShape();
            chainShape.createChain(collisionArea.getVertices());
            fixtureDef.shape = chainShape;
            body.createFixture(fixtureDef);
            chainShape.dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.2f, 0.1f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (directionChange) {
            player.applyLinearImpulse(
                    (xFactor * 3 - player.getLinearVelocity().x) * player.getMass(),
                    (yFactor * 3 - player.getLinearVelocity().y) * player.getMass(),
                    player.getWorldCenter().x, player.getWorldCenter().y, true
            );
        }

        viewport.apply(true);
        mapRenderer.setView(gameCamera);
        mapRenderer.render();
        box2DDebugRenderer.render(world, viewport.getCamera().combined);
        if (profiler.isEnabled()){
            Gdx.app.debug("RenderInfo", "Bindings " + profiler.getTextureBindings());
            Gdx.app.debug("RenderInfo", "Drawcalls " + profiler.getDrawCalls());
            profiler.reset();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
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
