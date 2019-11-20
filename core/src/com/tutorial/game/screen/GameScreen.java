package com.tutorial.game.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.tutorial.game.PreferenceManager;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeys;
import com.tutorial.game.input.InputManager;
import com.tutorial.game.map.Map;
import com.tutorial.game.map.MapListener;
import com.tutorial.game.map.MapManager;
import com.tutorial.game.type.MapType;
import com.tutorial.game.view.GameUI;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener {
    private final MapManager mapManager;
    private final PreferenceManager prefMgr;
    private final Entity player;

    public GameScreen(TutorialGame context) {
        super(context);

        mapManager = context.getMapManager();
        mapManager.setMap(MapType.MAP_1);
        prefMgr = context.getPreferenceManager();

        player = context.getEcsEngine().createPlayer(mapManager.getCurrentMap().getStartLocation(), 0.75f, 0.75f);
    }

    @Override
    protected GameUI getScreenUI(TutorialGame context) {
        return new GameUI(context);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            mapManager.setMap(MapType.MAP_1);
        }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            mapManager.setMap(MapType.MAP_2);
        }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
            prefMgr.saveGameState(player);
        }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
            prefMgr.loadGameState(player);
        }
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
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }

    @Override
    public void mapChange(Map currentMap) {

    }
}
