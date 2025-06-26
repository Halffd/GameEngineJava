package com.half;

public class Prism extends GameObject {
    private float width, height, depth;

    public Prism(String name, float width, float height, float depth) {
        super(name, generatePrismMesh(width, height, depth));
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.hasPhysics = true;
        this.bounds = new Vector3f(width * 0.5f, height * 0.5f, depth * 0.5f);
    }

    public Prism(String name) {
        this(name, 1.0f, 1.0f, 2.0f); // Default prism
    }

    private static Mesh generatePrismMesh(float width, float height, float depth) {
        // Dummy mesh: replace with real mesh generation logic
        float[] vertices = {
            // 6 vertices for a triangular prism (dummy values)
            0, 0, 0,
            width, 0, 0,
            width / 2, height, 0,
            0, 0, depth,
            width, 0, depth,
            width / 2, height, depth
        };
        return new Mesh(vertices);
    }

    @Override
    public void start() {
        System.out.println("Prism " + name + " spawned with size " + width + "x" + height + "x" + depth);
    }

    @Override
    public void update(float deltaTime) {
        // Prisms tumble
        rotation.z += 45 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Prism " + name + " collided with " + other.getName());
        velocity.x *= -0.7f;
        velocity.y *= -0.7f;
        velocity.z *= -0.7f;
    }
}
