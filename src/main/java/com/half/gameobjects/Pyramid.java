package com.half.gameobjects;

import com.half.GameObject;
import com.half.Mesh;
import com.half.GameObject;
import com.half.gameobjects.PyramidMesh;
import org.joml.Vector3f;

public class Pyramid extends GameObject {
    private float baseSize, height;

    public Pyramid(String name, float baseSize, float height) {
        super(name, new PyramidMesh(baseSize, height));
        this.baseSize = baseSize;
        this.height = height;
        this.hasPhysics = true;
        this.bounds = new Vector3f(baseSize * 0.5f, height * 0.5f, baseSize * 0.5f);
    }

    public Pyramid(String name) {
        this(name, 1.0f, 1.5f); // Default pyramid
    }

    @Override
    public void start() {
        System.out.println("Pyramid " + name + " erected with base " + baseSize + " and height " + height);
    }

    @Override
    public void update(float deltaTime) {
        // Pyramids spin majestically
        getTransform().getRotation().y += 60 * deltaTime;
        getTransform().getRotation().x += 10 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Pyramid " + name + " struck " + other.getName());
        getVelocity().x *= -0.6f;
        getVelocity().y *= -0.6f;
        getVelocity().z *= -0.6f;
    }
}
