package com.tutorial.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

import box2dLight.Light;

public class B2DComponent implements Component, Pool.Poolable {
    public Body body;
    public Light light;
    public float lightDistance;
    public float lightFluctuationDistance;
    public float lightFluctuationTime;
    public float lightFluctuationSpeed;
    public float width;
    public float height;
    public Vector2 renderPosition = new Vector2();

    @Override
    public void reset() {
        lightDistance = 0;
        lightFluctuationDistance = 0;
        lightFluctuationTime = 0;
        lightFluctuationSpeed = 0;
        if (light != null){
            light.remove();
            light = null;
        }
        if (body != null){
            body.getWorld().destroyBody(body);
            body = null;
        }
        width = height = 0;
        renderPosition.set(0,0);
    }
}
