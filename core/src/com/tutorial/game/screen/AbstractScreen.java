package com.tutorial.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.audio.AudioManager;
import com.tutorial.game.input.GameKeyInputListener;
import com.tutorial.game.input.InputManager;

public abstract class AbstractScreen<T extends Table> implements Screen, GameKeyInputListener {
    protected final TutorialGame context;
    protected final FitViewport viewport;
    protected final World world;
    protected final Box2DDebugRenderer box2DDebugRenderer;
    protected final Stage stage;
    protected final T screenUI;
    protected final InputManager inputManager;
    protected final AudioManager audioManager;

    protected AbstractScreen(final TutorialGame context) {
        this.context = context;
        this.viewport = context.getScreenViewport();
        this.world = context.getWorld();
        this.box2DDebugRenderer = context.getBox2DDebugRenderer();
        this.inputManager = context.getInputManager();

        stage = context.getStage();
        screenUI = getScreenUI(context);
        audioManager = context.getAudioManager();
    }

    protected abstract T getScreenUI(final TutorialGame context);

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        inputManager.addInputListener(this);
        stage.addActor(screenUI);
    }

    @Override
    public void hide() {
        inputManager.removeInputListener(this);
        stage.getRoot().removeActor(screenUI);
    }
}

