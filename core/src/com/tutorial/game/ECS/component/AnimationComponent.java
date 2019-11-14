package com.tutorial.game.ECS.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class AnimationComponent implements Component, Pool.Poolable {
    public AnimationType aniType;
    public float aniTime;
    public float width;
    public float height;
    @Override
    public void reset() {
        aniType = null;
        aniTime = 0;
        width = height = 0;
    }
}
