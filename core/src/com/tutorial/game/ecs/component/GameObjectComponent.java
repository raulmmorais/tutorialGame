package com.tutorial.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.tutorial.game.type.GameObjectType;

public class GameObjectComponent implements Component, Pool.Poolable {
    public int animationIndex;
    public GameObjectType type;

    @Override
    public void reset() {
        animationIndex = -1;
        type = null;
    }
}
