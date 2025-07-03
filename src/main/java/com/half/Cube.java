package com.half;

import org.joml.Vector3f;

/**
 * Enhanced Cube implementation that extends GameObject
 */
public class Cube extends GameObject {

    /**
     * Creates a new Cube with the given name and size
     */
    public Cube(String name, float size) {
        super(name, new CubeMesh(size));
        this.hasPhysics = true;
        this.bounds = new Vector3f(size * 0.5f, size * 0.5f, size * 0.5f);
    }

    @Override
    public void start() {
        // Initialization code if needed
    }
    
    @Override
    public void update(float deltaTime) {
        // Rotate the cube
        if (getRotationSpeed() != 0) {
            getTransform().getRotation().y = (getTransform().getRotation().y + getRotationSpeed() * deltaTime) % 360;
        }
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Handle collision with other game objects
    }
}
