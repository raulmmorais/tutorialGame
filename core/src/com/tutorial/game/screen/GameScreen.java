package com.tutorial.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.map.Map;
import com.tutorial.game.map.MapListener;
import com.tutorial.game.map.MapManager;
import com.tutorial.game.map.MapType;
import com.tutorial.game.view.GameUI;

import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener {
    private final MapManager mapManager;

    public GameScreen(TutorialGame context) {
        super(context);

        mapManager = context.getMapManager();
        mapManager.addMapListener(this);
        mapManager.setMap(MapType.MAP_2);

        context.getEcsEngine().createPlayer(mapManager.getCurrentMap().getStartLocator(), 0.5f, 0.5f);
    }

    @Override
    protected GameUI getScreenUI(TutorialGame context) {
        return new GameUI(context);
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            mapManager.setMap(MapType.MAP_1);
        }else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            mapManager.setMap(MapType.MAP_2);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }

    @Override
    public void mapChange(Map currentMap) {

    }
}
