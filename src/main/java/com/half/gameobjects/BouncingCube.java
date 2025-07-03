package com.half.gameobjects;

import com.half.Cube;
import org.joml.Vector3f;

/**
 * BouncingCube - extends Cube with screen boundary bouncing
 */
public class BouncingCube extends Cube {
    private final Vector3f screenBounds;
    private final float bounceReduction;

    public BouncingCube(String name, Vector3f screenBounds) {
        super(name, 1.0f); // Default size for CubeMesh
        this.screenBounds = screenBounds;
        this.bounceReduction = 0.9f; // Energy loss on bounce
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Bounce off screen boundaries
        if (Math.abs(getTransform().getPosition().x) > screenBounds.x / 2) {
            getTransform().getPosition().x = (screenBounds.x / 2) * Math.signum(getTransform().getPosition().x);
            getVelocity().x *= -bounceReduction;
        }
        if (Math.abs(getTransform().getPosition().y) > screenBounds.y / 2) {
            getTransform().getPosition().y = (screenBounds.y / 2) * Math.signum(getTransform().getPosition().y);
            getVelocity().y *= -bounceReduction;
        }
        if (Math.abs(getTransform().getPosition().z) > screenBounds.z / 2) {
            getTransform().getPosition().z = (screenBounds.z / 2) * Math.signum(getTransform().getPosition().z);
            getVelocity().z *= -bounceReduction;
        }
    }
}
