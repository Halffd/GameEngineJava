package com.half;

public class EnhancedGameObjectFactory {
    public static Cube createCube(String name, Vector3f position, Vector3f scale) {
        Cube cube = new Cube(name);
        cube.setPosition(position);
        cube.setScale(scale);
        return cube;
    }

    public static BouncingCube createBouncingCube(String name, Vector3f position, Vector3f velocity, Vector3f bounds) {
        BouncingCube cube = new BouncingCube(name, bounds);
        cube.setPosition(position);
        cube.setVelocity(velocity);
        return cube;
    }

    public static Sphere createSphere(String name, Vector3f position, float radius) {
        Sphere sphere = new Sphere(name, radius, 20, 20);
        sphere.setPosition(position);
        return sphere;
    }

    public static Sphere createSphere(String name, Vector3f position, Vector3f velocity, float radius) {
        Sphere sphere = new Sphere(name, radius, 20, 20);
        sphere.setPosition(position);
        sphere.setVelocity(velocity);
        return sphere;
    }

    public static Sphere createDetailedSphere(String name, Vector3f position, float radius, int detail) {
        Sphere sphere = new Sphere(name, radius, detail, detail);
        sphere.setPosition(position);
        return sphere;
    }

    public static Prism createPrism(String name, Vector3f position, float width, float height, float depth) {
        Prism prism = new Prism(name, width, height, depth);
        prism.setPosition(position);
        return prism;
    }

    public static Prism createPrism(String name, Vector3f position, Vector3f velocity) {
        Prism prism = new Prism(name);
        prism.setPosition(position);
        prism.setVelocity(velocity);
        return prism;
    }

    public static Pyramid createPyramid(String name, Vector3f position, float baseSize, float height) {
        Pyramid pyramid = new Pyramid(name, baseSize, height);
        pyramid.setPosition(position);
        return pyramid;
    }

    public static Pyramid createPyramid(String name, Vector3f position, Vector3f velocity) {
        Pyramid pyramid = new Pyramid(name);
        pyramid.setPosition(position);
        pyramid.setVelocity(velocity);
        return pyramid;
    }

    // Random shape generator - because chaos is fun
    public static GameObject createRandomShape(String name, Vector3f position, Vector3f velocity) {
        int shapeType = (int)(Math.random() * 4);
        GameObject obj;
        switch (shapeType) {
            case 0:
                obj = createCube(name, position, new Vector3f(1, 1, 1));
                break;
            case 1:
                obj = createSphere(name, position, 1.0f);
                break;
            case 2:
                obj = createPrism(name, position, 1.0f, 1.0f, 2.0f);
                break;
            case 3:
                obj = createPyramid(name, position, 1.0f, 1.5f);
                break;
            default:
                obj = createCube(name, position, new Vector3f(1, 1, 1));
                break;
        }
        obj.setVelocity(velocity);
        return obj;
    }
}
