package com.half;

import org.joml.Vector3f;
import java.util.List;
import java.util.ArrayList;

// Sphere GameObject with proper UV sphere generation
public class Sphere extends GameObject {
    private float radius;

    public Sphere(String name, float radius, int rings, int sectors) {
        super(name, new SphereMesh(radius, rings, sectors));
        this.radius = radius;
        this.hasPhysics = true;
        this.bounds = new Vector3f(radius, radius, radius);
    }

    public Sphere(String name) {
        this(name, 1.0f, 20, 20); // Default sphere
    }
    
    private static SphereMesh generateSphereMesh(float radius, int rings, int sectors) {
        if (rings < 2) rings = 2;
        if (sectors < 3) sectors = 3;
        List<Float> vertices = new ArrayList<>();
        float R = 1.0f / (float)(rings - 1);
        float S = 1.0f / (float)(sectors - 1);

        // Generate vertices (position only, no colors)
        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                float y = (float)Math.sin(-Math.PI / 2 + Math.PI * r * R);
                float x = (float)(Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
                float z = (float)(Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

                // Position only (3D coordinates)
                vertices.add(x * radius);
                vertices.add(y * radius);
                vertices.add(z * radius);
            }
        }


        // Generate indices for triangle strips
        List<Float> vertexData = new ArrayList<>();
        
        for (int r = 0; r < rings - 1; r++) {
            for (int s = 0; s < sectors; s++) {
                int curRow = r * sectors;
                int nextRow = (r + 1) * sectors;
                // Add two vertices from current ring and next ring to form a quad
                // First triangle of quad
                addVertex(vertices, vertexData, curRow + s);
                addVertex(vertices, vertexData, nextRow + s);
                addVertex(vertices, vertexData, curRow + ((s + 1) % sectors));
                
                // Second triangle of quad
                addVertex(vertices, vertexData, nextRow + s);
                addVertex(vertices, vertexData, nextRow + ((s + 1) % sectors));
                addVertex(vertices, vertexData, curRow + ((s + 1) % sectors));
            }
        }
        
        // Convert to float array
        float[] vertexArray = new float[vertexData.size()];
        for (int i = 0; i < vertexArray.length; i++) {
            vertexArray[i] = vertexData.get(i);
        }
        
        return new SphereMesh(radius, rings, sectors);
    }
    
    private static void addVertex(List<Float> src, List<Float> dst, int index) {
        // Add position (3 components)
        dst.add(src.get(index * 3));
        dst.add(src.get(index * 3 + 1));
        dst.add(src.get(index * 3 + 2));
    }

    @Override
    public void start() {
        System.out.println("Sphere " + name + " spawned with radius " + radius);
    }

    @Override
    public void update(float deltaTime) {
        // Gentle rotation
        getTransform().getRotation().y += 30 * deltaTime;
        getTransform().getRotation().x += 15 * deltaTime;
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Sphere " + name + " bounced off " + other.getName());
        // Elastic collision for spheres
        getVelocity().x *= -0.95f;
        getVelocity().y *= -0.95f;
        getVelocity().z *= -0.95f;
    }

    public float getRadius() { 
        return radius; 
    }
}
