package com.half.gameobjects;

import com.half.CubeMesh;
import com.half.GameObject;

public class Cube extends GameObject {

    public Cube(String name, float size) {
        super(name, new CubeMesh(size));
    }

    @Override
    public void start() {
        // Initialization logic for Cube, if any
    }

    @Override
    public void update(float deltaTime) {
        // Update logic for Cube, if any
    }

    @Override
    public void onCollision(GameObject other) {
        // Collision handling for Cube, if any
    }
}
