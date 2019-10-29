package com.tutorial.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.screen.AbstractScreen;
import com.tutorial.game.screen.LoadingScreen;
import com.tutorial.game.screen.ScreenType;

import java.util.EnumMap;

public class TutorialGame extends Game {
	private static final String TAG = TutorialGame.class.getSimpleName();

	private EnumMap<ScreenType, AbstractScreen> screenCache;
	private FitViewport screenViewport;

	public static final short BIT_PLAYER = 1 << 0;
	public static final short BIT_BOX = 1 << 1;
	public static final short BIT_GROUND = 1 << 2;
	private World world;
	private WorldContactListener worldContactListener;
	private Box2DDebugRenderer box2DDebugRenderer;

	private static final float FIXED_TIME = 1/60f;
	private float accumulator;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		accumulator = 0;

		Box2D.init();
		worldContactListener = new WorldContactListener();
		world = new World(new Vector2(0, -9.8f), true);
		world.setContactListener(worldContactListener);
		box2DDebugRenderer = new Box2DDebugRenderer();

		screenViewport = new FitViewport(9, 16);
		screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
		setScreen(ScreenType.GAME);
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

		accumulator += Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		while (accumulator >= FIXED_TIME){
			world.step(FIXED_TIME, 6, 2);
			accumulator -= FIXED_TIME;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		box2DDebugRenderer.dispose();
	}
}
