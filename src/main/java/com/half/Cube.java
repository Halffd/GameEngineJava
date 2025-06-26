package com.half;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Enhanced Cube implementation that extends GameObject
 */
public class Cube extends GameObject {
    private static final float[] VERTICES = {
        // Front face
        -0.5f, -0.5f,  0.5f,  // 0: bottom-left-front
         0.5f, -0.5f,  0.5f,  // 1: bottom-right-front
         0.5f,  0.5f,  0.5f,  // 2: top-right-front
        -0.5f,  0.5f,  0.5f,  // 3: top-left-front
        
        // Back face
        -0.5f, -0.5f, -0.5f,  // 4: bottom-left-back
         0.5f, -0.5f, -0.5f,  // 5: bottom-right-back
         0.5f,  0.5f, -0.5f,  // 6: top-right-back
        -0.5f,  0.5f, -0.5f   // 7: top-left-back
    };

    private static final int[] INDICES = {
        // Front face
        0, 1, 2, 2, 3, 0,
        // Right face
        1, 5, 6, 6, 2, 1,
        // Back face
        5, 4, 7, 7, 6, 5,
        // Left face
        4, 0, 3, 3, 7, 4,
        // Top face
        3, 2, 6, 6, 7, 3,
        // Bottom face
        4, 5, 1, 1, 0, 4
    };

    private float rotationSpeed;
    private float colorTime;

    /**
     * Creates a new Cube with the given name
     */
    public Cube(String name) {
        super(name, new Mesh(VERTICES));
        this.rotationSpeed = 45.0f; // degrees per second
        this.colorTime = 0.0f;
        this.hasPhysics = true;
        this.bounds = new Vector3f(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void start() {
        // Initialization code if needed
    }
    
    @Override
    public void update(float deltaTime) {
        // Rotate the cube
        if (rotationSpeed != 0) {
            rotation.y = (rotation.y + rotationSpeed * deltaTime) % 360;
        }
        
        // Update color over time
        colorTime += deltaTime;
        if (colorTime > 5.0f) {
            colorTime = 0.0f;
        }
        
        // Simple color cycling effect
        float r = (float) (0.5f + 0.5f * Math.sin(colorTime * 0.5f));
        float g = (float) (0.5f + 0.5f * Math.sin(colorTime * 0.7f + 2.0f));
        float b = (float) (0.5f + 0.5f * Math.sin(colorTime * 0.3f + 4.0f));
        
        // Set the color using the parent class method if available
        if (this instanceof Renderable) {
            ((Renderable)this).setColor(r, g, b, 1.0f);
        }
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Handle collision with other game objects
    }
    
    @Override
    public void setColor(float r, float g, float b, float a) {
        if (this.mesh != null) {
            this.mesh.setColor(r, g, b, a);
        }
    }
}
