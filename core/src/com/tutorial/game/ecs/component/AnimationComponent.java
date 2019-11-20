package com.tutorial.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.tutorial.game.type.AnimationType;

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
