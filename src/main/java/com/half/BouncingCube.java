package com.half;

import org.joml.Vector3f;

/**
 * BouncingCube - extends Cube with screen boundary bouncing
 */
public class BouncingCube extends Cube {
    private Vector3f screenBounds;
    private float bounceReduction;
    
    public BouncingCube(String name, Vector3f screenBounds) {
        super(name, 1.0f); // Default size for CubeMesh
        this.screenBounds = screenBounds;
        this.bounceReduction = 0.9f; // Energy loss on bounce
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Check bounds and bounce if needed
        if (getTransform().getPosition().x - getBounds().x < -screenBounds.x || getTransform().getPosition().x + getBounds().x > screenBounds.x) {
            getVelocity().x = -getVelocity().x * bounceReduction;
            getTransform().getPosition().x = Math.max(-screenBounds.x + getBounds().x, Math.min(screenBounds.x - getBounds().x, getTransform().getPosition().x));
        }
        
        if (getTransform().getPosition().y - getBounds().y < -screenBounds.y || getTransform().getPosition().y + getBounds().y > screenBounds.y) {
            getVelocity().y = -getVelocity().y * bounceReduction;
            getTransform().getPosition().y = Math.max(-screenBounds.y + getBounds().y, Math.min(screenBounds.y - getBounds().y, getTransform().getPosition().y));
        }
        
        if (getTransform().getPosition().z - getBounds().z < -screenBounds.z || getTransform().getPosition().z + getBounds().z > screenBounds.z) {
            getVelocity().z = -getVelocity().z * bounceReduction;
            getTransform().getPosition().z = Math.max(-screenBounds.z + getBounds().z, Math.min(screenBounds.z - getBounds().z, getTransform().getPosition().z));
        }
    }
}
