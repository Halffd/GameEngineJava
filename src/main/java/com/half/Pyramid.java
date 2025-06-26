package com.half;

public class Pyramid extends GameObject {
    private float baseSize, height;

    public Pyramid(String name, float baseSize, float height) {
        super(name, generatePyramidMesh(baseSize, height));
        this.baseSize = baseSize;
        this.height = height;
        this.hasPhysics = true;
        this.bounds = new Vector3f(baseSize * 0.5f, height * 0.5f, baseSize * 0.5f);
    }

    public Pyramid(String name) {
        this(name, 1.0f, 1.5f); // Default pyramid
    }

    private static Mesh generatePyramidMesh(float baseSize, float height) {
        // Dummy mesh: replace with real mesh generation logic
        float[] vertices = {
            // 5 vertices for a square base pyramid (dummy values)
            0, 0, 0,
            baseSize, 0, 0,
            baseSize, 0, baseSize,
            0, 0, baseSize,
            baseSize / 2, height, baseSize / 2
        };
        return new Mesh(vertices);
    }

    @Override
    public void start() {
        System.out.println("Pyramid " + name + " erected with base " + baseSize + " and height " + height);
    }

    @Override
    public void update(float deltaTime) {
        // Pyramids spin majestically
        rotation.y += 60 * deltaTime;
        rotation.x += 10 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Pyramid " + name + " struck " + other.getName());
        velocity.x *= -0.6f;
        velocity.y *= -0.6f;
        velocity.z *= -0.6f;
    }
}
