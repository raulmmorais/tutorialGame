package com.tutorial.game.ECS.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {
    public boolean hasAxe;

    @Override
    public void reset() {
        hasAxe = false;
    }
}
