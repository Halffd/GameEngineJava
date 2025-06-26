package com.half;

import org.joml.Vector3f;

/**
 * BouncingCube - extends Cube with screen boundary bouncing
 */
public class BouncingCube extends Cube {
    private Vector3f screenBounds;
    private float bounceReduction;
    
    public BouncingCube(String name, Vector3f screenBounds) {
        super(name);
        this.screenBounds = screenBounds;
        this.bounceReduction = 0.9f; // Energy loss on bounce
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Check bounds and bounce if needed
        if (position.x - bounds.x < -screenBounds.x || position.x + bounds.x > screenBounds.x) {
            velocity.x = -velocity.x * bounceReduction;
            position.x = Math.max(-screenBounds.x + bounds.x, Math.min(screenBounds.x - bounds.x, position.x));
        }
        
        if (position.y - bounds.y < -screenBounds.y || position.y + bounds.y > screenBounds.y) {
            velocity.y = -velocity.y * bounceReduction;
            position.y = Math.max(-screenBounds.y + bounds.y, Math.min(screenBounds.y - bounds.y, position.y));
        }
        
        if (position.z - bounds.z < -screenBounds.z || position.z + bounds.z > screenBounds.z) {
            velocity.z = -velocity.z * bounceReduction;
            position.z = Math.max(-screenBounds.z + bounds.z, Math.min(screenBounds.z - bounds.z, position.z));
        }
    }
}
