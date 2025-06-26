package com.half;

import com.half.gameobjects.BouncingCube;
import org.joml.Vector3f;

/**
 * GameObjectFactory - simplifies creating game objects
 */
public class GameObjectFactory {
    
    /**
     * Creates a new Cube game object
     */
    public static Cube createCube(String name, Vector3f position, Vector3f scale) {
        Cube cube = new Cube(name);
        cube.setPosition(position);
        cube.setScale(scale);
        return cube;
    }
    
    /**
     * Creates a new BouncingCube game object
     */
    public static BouncingCube createBouncingCube(String name, Vector3f position, Vector3f velocity, Vector3f bounds) {
        BouncingCube cube = new BouncingCube(name, bounds);
        cube.setPosition(position);
        cube.setVelocity(velocity);
        return cube;
    }
    
    /**
     * Creates a simple sphere (placeholder for future implementation)
     */
    public static GameObject createSphere(String name) {
        // TODO: Implement sphere creation
        throw new UnsupportedOperationException("Sphere creation not implemented yet");
    }
}
