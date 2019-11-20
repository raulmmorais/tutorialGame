package com.tutorial.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.audio.AudioManager;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.map.MapManager;
import com.tutorial.game.screen.AbstractScreen;
import com.tutorial.game.type.ScreenType;
import com.tutorial.game.view.GameRenderer;

import java.util.EnumMap;

import box2dLight.Light;
import box2dLight.RayHandler;

public class TutorialGame extends Game {
	private static final String TAG = TutorialGame.class.getSimpleName();

	private SpriteBatch spriteBatch;
	private EnumMap<ScreenType, AbstractScreen> screenCache;
	private OrthographicCamera gameCamera;
	private FitViewport screenViewport;

	public static final BodyDef BODY_DEF = new BodyDef();
	public static final FixtureDef FIXTURE_DEF = new FixtureDef();
	public static final float UNIT_SCALE = 1 / 32f;
	public static final short BIT_PLAYER = 1 << 1;
	public static final short BIT_GROUND = 1 << 2;
	public static final short BIT_GAME_OBJECT = 1 << 3;
	private World world;
	private WorldContactListener worldContactListener;
	private RayHandler rayHandler;

	private static final float FIXED_TIME_STEP = 1/60f;
	private float accumulator;

	private AssetManager assetManager;
	private AudioManager audioManager;

	private Stage stage;
	private Skin skin;
	private I18NBundle i18NBundle;

	private InputManager inputManager;

	private MapManager mapManager;

	private ECSEngine ecsEngine;

	private GameRenderer gameRenderer;

	private PreferenceManager preferenceManager;

	private Box2DDebugRenderer box2DDebugRenderer;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();

		//box 2d
		accumulator = 0;
		Box2D.init();
		world = new World(Vector2.Zero, true);
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0, 0, 0, 0.1f);
		Light.setGlobalContactFilter(BIT_PLAYER, (short)1, BIT_GROUND);

		//initialize assetManager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
		initializeSkin();
		stage = new Stage(new FitViewport(450, 800), spriteBatch);

		//audio
		audioManager = new AudioManager(this);

		//input
		inputManager = new InputManager();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager, stage));

		//game viewport
		gameCamera = new OrthographicCamera();
		screenViewport = new FitViewport(9, 16, gameCamera);
		//engine
		ecsEngine = new ECSEngine(this);
		//map manager
		mapManager = new MapManager(this);
		//game renderer
		gameRenderer = new GameRenderer(this);

		preferenceManager = new PreferenceManager();

		//set fist screen
		screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
		setScreen(ScreenType.LOADING);
		box2DDebugRenderer = new Box2DDebugRenderer();
	}

	public static void resetBodyAndFixtureDefinition(){
		BODY_DEF.position.set(0, 0);
		BODY_DEF.gravityScale = 1;
		BODY_DEF.type = BodyDef.BodyType.StaticBody;
		BODY_DEF.fixedRotation = false;

		FIXTURE_DEF.density = 0;
		FIXTURE_DEF.isSensor = false;
		FIXTURE_DEF.restitution = 0;
		FIXTURE_DEF.friction = 0.2f;
		FIXTURE_DEF.filter.categoryBits = 0x0001;
		FIXTURE_DEF.filter.maskBits = -1;
		FIXTURE_DEF.shape = null;
	}

	private void initializeSkin() {
		// setup collors
		Colors.put("Red", Color.RED);
		Colors.put("BLUE", Color.BLUE);
		Colors.put("STEEL_BLUE", new Color(70/255.0f,130/255.0f,180/255.0f, 1));

		//generate bitmaps
		final ObjectMap<String, Object> resources = new ObjectMap<>();
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font2.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;
		final int[] sizesToCreate = {16, 20, 26, 32};
		for(int size: sizesToCreate){
			fontParameter.size = size;
			final BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
			bitmapFont.getData().markupEnabled = true;
			resources.put("font_"+size, bitmapFont);
		}
		fontGenerator.dispose();

		//load skin
		final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("ui/hud.atlas", resources);
		assetManager.load("ui/hud.json", Skin.class, skinParameter);
		assetManager.load("ui/strings", I18NBundle.class);
		assetManager.finishLoading();
		skin = assetManager.get("ui/hud.json", Skin.class);
		i18NBundle = assetManager.get("ui/strings", I18NBundle.class);
	}

	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public ECSEngine getEcsEngine() {
		return ecsEngine;
	}

	public AudioManager getAudioManager() {
        return audioManager;
    }

    public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}

	public InputManager getInputManager() {
		return inputManager;
	}

	public I18NBundle getI18NBundle() {
		return i18NBundle;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public OrthographicCamera getGameCamera() {
		return gameCamera;
	}

	public FitViewport getScreenViewport() {
		return screenViewport;
	}

	public WorldContactListener getWorldContactListener() {
		return worldContactListener;
	}

	public World getWorld() {
		return world;
	}

	public Box2DDebugRenderer getBox2DDebugRenderer() {
		return box2DDebugRenderer;
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public void setScreen(final ScreenType screenType) {
		final Screen screen = screenCache.get(screenType);
		if (screen == null){
			try {
				Gdx.app.debug(TAG, "Creating new screen: " + screenType);
				final AbstractScreen newScreen = (AbstractScreen) ClassReflection.getConstructor(screenType.getScreenClass(), TutorialGame.class).newInstance(this);
				screenCache.put(screenType, newScreen);
				setScreen(newScreen);
			} catch (ReflectionException e) {
				throw new GdxRuntimeException("Screen "+ screenType + "Could not be created ", e);
			}
		}else {
			Gdx.app.debug(TAG, "Switching to screen " + screenType);
			setScreen(screen);
		}
	}

	@Override
	public void render() {
		super.render();

		final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		ecsEngine.update(deltaTime);
		accumulator += deltaTime;
		while (accumulator >= FIXED_TIME_STEP){
			world.step(FIXED_TIME_STEP, 6, 2);
			accumulator -= FIXED_TIME_STEP;
		}

		gameRenderer.render(accumulator / FIXED_TIME_STEP);
		stage.getViewport().apply();
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		gameRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		assetManager.dispose();
		spriteBatch.dispose();
		stage.dispose();
		box2DDebugRenderer.dispose();
	}
}