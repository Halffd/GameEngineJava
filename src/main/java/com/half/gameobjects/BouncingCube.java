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
        super(name);
        this.screenBounds = screenBounds;
        this.bounceReduction = 0.9f; // Energy loss on bounce
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Bounce off screen boundaries
        if (Math.abs(position.x) > screenBounds.x / 2) {
            position.x = (screenBounds.x / 2) * Math.signum(position.x);
            velocity.x *= -bounceReduction;
        }
        if (Math.abs(position.y) > screenBounds.y / 2) {
            position.y = (screenBounds.y / 2) * Math.signum(position.y);
            velocity.y *= -bounceReduction;
        }
        if (Math.abs(position.z) > screenBounds.z / 2) {
            position.z = (screenBounds.z / 2) * Math.signum(position.z);
            velocity.z *= -bounceReduction;
        }
    }
}
