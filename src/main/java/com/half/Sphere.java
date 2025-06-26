package com.half;

import java.util.List;
import java.util.ArrayList;

// Sphere GameObject with proper UV sphere generation
public class Sphere extends GameObject {
    private int rings;
    private int sectors;
    private float radius;

    public Sphere(String name, float radius, int rings, int sectors) {
        super(name, generateSphereMesh(radius, rings, sectors));
        this.radius = radius;
        this.rings = rings;
        this.sectors = sectors;
        this.hasPhysics = true;
        this.bounds = new Vector3f(radius, radius, radius);
    }

    public Sphere(String name) {
        this(name, 1.0f, 20, 20); // Default sphere
    }

    private static Mesh generateSphereMesh(float radius, int rings, int sectors) {
        List<Float> vertices = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float R = 1.0f / (float)(rings - 1);
        float S = 1.0f / (float)(sectors - 1);

        // Generate vertices and colors
        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                float y = (float)Math.sin(-Math.PI / 2 + Math.PI * r * R);
                float x = (float)(Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
                float z = (float)(Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

                // Position
                vertices.add(x * radius);
                vertices.add(y * radius);
                vertices.add(z * radius);

                // Color based on position (makes it look cool)
                colors.add((x + 1) * 0.5f);
                colors.add((y + 1) * 0.5f);
                colors.add((z + 1) * 0.5f);
            }
        }

        // Generate indices
        for (int r = 0; r < rings - 1; r++) {
            for (int s = 0; s < sectors - 1; s++) {
                int curRow = r * sectors;
                int nextRow = (r + 1) * sectors;

                // First triangle
                indices.add(curRow + s);
                indices.add(nextRow + s);
                indices.add(nextRow + (s + 1));

                // Second triangle
                indices.add(curRow + s);
                indices.add(nextRow + (s + 1));
                indices.add(curRow + (s + 1));
            }
        }

        // Convert lists to arrays
        float[] vertexArray = new float[vertices.size()];
        float[] colorArray = new float[colors.size()];
        int[] indexArray = new int[indices.size()];

        for (int i = 0; i < vertices.size(); i++) vertexArray[i] = vertices.get(i);
        for (int i = 0; i < colors.size(); i++) colorArray[i] = colors.get(i);
        for (int i = 0; i < indices.size(); i++) indexArray[i] = indices.get(i);

        return new Mesh(vertexArray, colorArray, indexArray);
    }

    @Override
    public void start() {
        System.out.println("Sphere " + name + " spawned with radius " + radius);
    }

    @Override
    public void update(float deltaTime) {
        // Gentle rotation
        rotation.y += 30 * deltaTime;
        rotation.x += 15 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Sphere " + name + " bounced off " + other.getName());
        // Elastic collision for spheres
        velocity.x *= -0.95f;
        velocity.y *= -0.95f;
        velocity.z *= -0.95f;
    }

    public float getRadius() { return radius; }
}

// Prism (triangular prism) GameObject
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
        float w = width * 0.5f;
        float h = height * 0.5f;
        float d = depth * 0.5f;

        float[] vertices = {
                // Front triangular face
                0.0f,  h, d,    // Top vertex
                -w,   -h, d,    // Bottom left
                w,   -h, d,    // Bottom right

                // Back triangular face
                0.0f,  h, -d,   // Top vertex
                w,   -h, -d,   // Bottom right
                -w,   -h, -d,   // Bottom left

                // Bottom rectangular face
                -w, -h,  d,     // Front left
                w, -h,  d,     // Front right
                w, -h, -d,     // Back right
                -w, -h, -d,     // Back left

                // Left rectangular face
                -w, -h,  d,     // Front bottom
                -w, -h, -d,     // Back bottom
                0,  h, -d,     // Back top
                0,  h,  d,     // Front top

                // Right rectangular face
                w, -h,  d,     // Front bottom
                0,  h,  d,     // Front top
                0,  h, -d,     // Back top
                w, -h, -d      // Back bottom
        };

        float[] colors = {
                // Front face - Red gradient
                1.0f, 0.5f, 0.5f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,
                // Back face - Blue gradient
                0.5f, 0.5f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,
                // Bottom face - Green
                0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,
                // Left face - Yellow gradient
                1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.5f,  1.0f, 1.0f, 0.5f,
                // Right face - Magenta gradient
                1.0f, 0.0f, 1.0f,  1.0f, 0.5f, 1.0f,  1.0f, 0.5f, 1.0f,  1.0f, 0.0f, 1.0f
        };

        int[] indices = {
                // Front triangular face
                0, 1, 2,
                // Back triangular face
                3, 4, 5,
                // Bottom rectangular face
                6, 7, 8,  6, 8, 9,
                // Left rectangular face
                10, 11, 12,  10, 12, 13,
                // Right rectangular face
                14, 15, 16,  14, 16, 17
        };

        return new Mesh(vertices, colors, indices);
    }

    @Override
    public void start() {
        System.out.println("Prism " + name + " created with dimensions " + width + "x" + height + "x" + depth);
    }

    @Override
    public void update(float deltaTime) {
        // Spin around Y axis primarily
        rotation.y += 45 * deltaTime;
        rotation.z += 15 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Prism " + name + " collided with " + other.getName());
        // Prisms tumble more on collision
        velocity.x *= -0.7f;
        velocity.y *= -0.7f;
        velocity.z *= -0.7f;
        rotation.x += (float)(Math.random() - 0.5) * 90;
        rotation.z += (float)(Math.random() - 0.5) * 90;
    }
}

// Pyramid GameObject (square base pyramid)
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
        float s = baseSize * 0.5f; // Half base size
        float h = height * 0.5f;   // Half height

        float[] vertices = {
                // Base vertices (square)
                -s, -h, -s,  // 0: Back left
                s, -h, -s,  // 1: Back right
                s, -h,  s,  // 2: Front right
                -s, -h,  s,  // 3: Front left

                // Apex vertex
                0,  h,  0,  // 4: Top
        };

        float[] colors = {
                // Base colors - earthy tones
                0.6f, 0.4f, 0.2f,  // Brown
                0.8f, 0.6f, 0.4f,  // Light brown
                0.7f, 0.5f, 0.3f,  // Medium brown
                0.5f, 0.3f, 0.1f,  // Dark brown

                // Apex color - golden
                1.0f, 0.8f, 0.0f   // Gold
        };

        int[] indices = {
                // Base (two triangles)
                0, 1, 2,  0, 2, 3,

                // Side faces (triangles from base edges to apex)
                0, 4, 1,  // Back face
                1, 4, 2,  // Right face
                2, 4, 3,  // Front face
                3, 4, 0   // Left face
        };

        return new Mesh(vertices, colors, indices);
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
        // Pyramids are heavy and stable
        velocity.x *= -0.6f;
        velocity.y *= -0.6f;
        velocity.z *= -0.6f;
    }
}

// Enhanced GameObjectFactory with new shapes
public class EnhancedGameObjectFactory {

    // Cube methods (existing)
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

    // Sphere methods
    public static Sphere createSphere(String name, Vector3f position, float radius) {
        Sphere sphere = new Sphere(name, radius, 20, 20);
        sphere.setPosition(position);
        return sphere;
    }

    public static Sphere createSphere(String name, Vector3f position, Vector3f velocity, float radius) {
        Sphere sphere = createSphere(name, position, radius);
        sphere.setVelocity(velocity);
        return sphere;
    }

    public static Sphere createDetailedSphere(String name, Vector3f position, float radius, int detail) {
        Sphere sphere = new Sphere(name, radius, detail, detail);
        sphere.setPosition(position);
        return sphere;
    }

    // Prism methods
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

    // Pyramid methods
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
