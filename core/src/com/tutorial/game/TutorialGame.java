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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.ECS.ECSEngine;
import com.tutorial.game.audio.AudioManager;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.screen.AbstractScreen;
import com.tutorial.game.screen.LoadingScreen;
import com.tutorial.game.screen.ScreenType;

import java.util.EnumMap;

public class TutorialGame extends Game {
	private static final String TAG = TutorialGame.class.getSimpleName();

	public static final float UNIT_SCALE = 1 / 32;
	public static final short BIT_PLAYER = 1 << 1;
	public static final short BIT_BOX = 1 << 2;
	public static final short BIT_GROUND = 1 << 3;

	private AssetManager assetManager;
	private SpriteBatch spriteBatch;
	private WorldContactListener worldContactListener;
	private EnumMap<ScreenType, AbstractScreen> screenCache;
	private OrthographicCamera camera;
	private FitViewport screenViewport;
	private World world;
	private Box2DDebugRenderer box2DDebugRenderer;

	private static final float FIXED_TIME = 1/60f;
	private float accumulator;

	private Stage stage;
	private Skin skin;
	private I18NBundle i18NBundle;

	private InputManager inputManager;

	private AudioManager audioManager;

	private ECSEngine ecsEngine;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();

		accumulator = 0;
		Box2D.init();
		world = new World(Vector2.Zero, true);
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);
		box2DDebugRenderer = new Box2DDebugRenderer();

		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));

		audioManager = new AudioManager(this);

		initializeSkin();
		stage = new Stage(new FitViewport(450, 800), spriteBatch);

		inputManager = new InputManager();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager, stage));

		ecsEngine = new ECSEngine(this);
		camera = new OrthographicCamera();
		screenViewport = new FitViewport(9, 16, camera);
		screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
		setScreen(ScreenType.LOADING);
	}

	private void initializeSkin() {
		Colors.put("Red", Color.RED);
		Colors.put("BLUE", Color.BLUE);
		Colors.put("STEEL_BLUE", new Color(70/255.0f,130/255.0f,180/255.0f, 1));

		final ObjectMap<String, Object> resources = new ObjectMap<>();
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font2.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;
		final int[] sizes = {16, 20, 26, 32};
		for(int size: sizes){
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

	public OrthographicCamera getCamera() {
		return camera;
	}

	public FitViewport getScreenViewport() {
		return screenViewport;
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
				Gdx.app.debug(TAG, "Creating a new Screen " + screenType);
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

		ecsEngine.update(Gdx.graphics.getRawDeltaTime());
		accumulator += Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		while (accumulator >= FIXED_TIME){
			world.step(FIXED_TIME, 6, 2);
			accumulator -= FIXED_TIME;
		}

		stage.getViewport().apply();
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		box2DDebugRenderer.dispose();
		world.dispose();
		assetManager.dispose();
		 spriteBatch.dispose();
		 stage.dispose();
	}
}
