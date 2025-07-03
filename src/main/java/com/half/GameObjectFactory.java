package com.half;

import com.half.gameobjects.BouncingCube;
import com.half.gameobjects.Cube;
import com.half.gameobjects.Sphere;
import com.half.gameobjects.Prism;
import com.half.gameobjects.Pyramid;
import org.joml.Vector3f;

/**
 * GameObjectFactory - simplifies creating game objects
 */
public class GameObjectFactory {
    
    /**
     * Creates a new Cube game object
     */
    public static Cube createCube(String name, Vector3f position, Vector3f scale) {
        Cube cube = new Cube(name, scale.x); // Assuming uniform scale for simplicity
        cube.getTransform().setPosition(position);
        cube.getTransform().setScale(scale);
        return cube;
    }
    
    /**
     * Creates a new BouncingCube game object
     */
    public static BouncingCube createBouncingCube(String name, Vector3f position, Vector3f velocity, Vector3f bounds) {
        BouncingCube cube = new BouncingCube(name, bounds);
        cube.getTransform().setPosition(position);
        cube.setVelocity(velocity);
        return cube;
    }
    
    /**
     * Creates a simple sphere (placeholder for future implementation)
     */
    public static Sphere createSphere(String name, Vector3f position, float radius) {
        Sphere sphere = new Sphere(name, radius, 20, 20);
        sphere.getTransform().setPosition(position);
        return sphere;
    }

    public static Sphere createSphere(String name, Vector3f position, Vector3f velocity, float radius) {
        Sphere sphere = new Sphere(name, radius, 20, 20);
        sphere.getTransform().setPosition(position);
        sphere.setVelocity(velocity);
        return sphere;
    }

    public static Sphere createDetailedSphere(String name, Vector3f position, float radius, int detail) {
        Sphere sphere = new Sphere(name, radius, detail, detail);
        sphere.getTransform().setPosition(position);
        return sphere;
    }

    public static Pyramid createPyramid(String name, Vector3f position, float baseSize, float height) {
        Pyramid pyramid = new Pyramid(name, baseSize, height);
        pyramid.getTransform().setPosition(position);
        return pyramid;
    }

    public static Prism createPrism(String name, Vector3f position, float width, float height, float depth) {
        Prism prism = new Prism(name, width, height, depth);
        prism.getTransform().setPosition(position);
        return prism;
    }
}
