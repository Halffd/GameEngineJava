package com.half.gameobjects;

import com.half.gameobjects.PrismMesh;
import org.joml.Vector3f;

public class Prism extends GameObject {
    private float width, height, depth;

    public Prism(String name, float width, float height, float depth) {
        super(name, new PrismMesh(width, height, depth));
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.hasPhysics = true;
        this.bounds = new Vector3f(width * 0.5f, height * 0.5f, depth * 0.5f);
    }

    public Prism(String name) {
        this(name, 1.0f, 1.0f, 2.0f); // Default prism
    }

    @Override
    public void start() {
        System.out.println("Prism " + name + " spawned with size " + width + "x" + height + "x" + depth);
    }

    @Override
    public void update(float deltaTime) {
        // Prisms tumble
        getTransform().getRotation().z += 45 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Prism " + name + " collided with " + other.getName());
        getVelocity().x *= -0.7f;
        getVelocity().y *= -0.7f;
        getVelocity().z *= -0.7f;
    }
}
