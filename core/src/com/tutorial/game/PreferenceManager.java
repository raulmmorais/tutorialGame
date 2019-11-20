package com.tutorial.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tutorial.game.ecs.ECSEngine;
import com.tutorial.game.ecs.component.B2DComponent;


public class PreferenceManager implements Json.Serializable{
    private final Preferences preferences;
    private final Json json;
    private final JsonReader jsonReader;

    private final Vector2 playerPos;

    public PreferenceManager() {
        playerPos = new Vector2();
        jsonReader = new JsonReader();
        json = new Json();
        preferences = Gdx.app.getPreferences("tutorialGame");
    }

    public boolean containsKey(final String key){
        return preferences.contains(key);
    }

    public void setFloatValue(final String key, final float value){
        preferences.putFloat(key, value);
        preferences.flush();
    }

    public void saveGameState(final Entity player){
        playerPos.set(ECSEngine.b2dCmpMapper.get(player).body.getPosition());
        preferences.putString("GAME_STATE", new Json().toJson(this));
        preferences.flush();
    }

    public void loadGameState(final Entity player){
        final JsonValue savedJsonString = jsonReader.parse(preferences.getString("GAME_STATE"));

        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(player);
        b2DComponent.body.setTransform(savedJsonString.getFloat("PLAYER_X", 0f), savedJsonString.getFloat("PLAYER_Y", 0f), b2DComponent.body.getAngle());
    }

    @Override
    public void write(Json json) {
        json.writeValue("PLAYER_X", playerPos.x);
        json.writeValue("PLAYER_Y", playerPos.y);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }
}
