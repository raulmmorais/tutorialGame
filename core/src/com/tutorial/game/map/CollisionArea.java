package com.tutorial.game.map;

import static com.tutorial.game.TutorialGame.UNIT_SCALE;

public class CollisionArea {
    private final float x;
    private final float y;
    private final float[] vertices;

    public CollisionArea(final float x, final float y, final float[] vertices) {
        this.x = (x * UNIT_SCALE);
        this.y = (y * UNIT_SCALE);
        this.vertices = vertices;
        for (int i = 0; i < vertices.length; i++){
            vertices[i] *= UNIT_SCALE;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float[] getVertices() {
        return vertices;
    }
}
