package com.half.gameobjects;

import com.half.GameObject;
import com.half.SphereMesh;

public class Sphere extends GameObject {

    public Sphere(String name, float radius, int sectorCount, int stackCount) {
        super(name, new SphereMesh(radius, sectorCount, stackCount));
    }

    @Override
    public void start() {
        // Initialization logic for Sphere, if any
    }

    @Override
    public void update(float deltaTime) {
        // Update logic for Sphere, if any
    }

    @Override
    public void onCollision(GameObject other) {
        // Collision handling for Sphere, if any
    }
}
