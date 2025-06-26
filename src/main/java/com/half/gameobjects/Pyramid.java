package com.half.gameobjects;

import com.half.GameObject;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Pyramid extends GameObject {
    private int vaoId;
    private int vboId;
    private int eboId;
    private float baseSize;
    private float height;
    private Vector4f color;
    private float rotationSpeed;
    private float baseScale;

    public Pyramid(String name, float baseSize, float height) {
        super(name);
        this.baseSize = baseSize;
        this.height = height;
        this.baseScale = 1.0f;
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.rotationSpeed = 1.0f;
        init();
    }

    private void init() {
        // Pyramid vertices (positions and normals)
        // Base vertices (z = -height/2)
        float hs = baseSize * 0.5f;
        float hh = height * 0.5f;
        
        float[] vertices = {
            // Base (square)
            -hs, -hh, -hh,  0.0f, -1.0f,  0.0f,  // Bottom-left
             hs, -hh, -hh,  0.0f, -1.0f,  0.0f,  // Bottom-right
             hs, -hh,  hh,  0.0f, -1.0f,  0.0f,  // Top-right
            -hs, -hh,  hh,  0.0f, -1.0f,  0.0f,  // Top-left
            
            // Apex
             0.0f,  hh,  0.0f,  0.0f,  0.0f,  1.0f  // Top point
        };

        // Indices for drawing the pyramid (12 triangles = 36 indices)
        int[] indices = {
            // Base (2 triangles)
            0, 1, 2,  2, 3, 0,
            
            // Sides (4 triangles)
            0, 1, 4,  // Front
            1, 2, 4,  // Right
            2, 3, 4,  // Back
            3, 0, 4   // Left
        };

        // Create VAO and bind it
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO for vertices and normals
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute (location = 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Normal attribute (location = 1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Create EBO for indices
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Unbind VAO (and VBO + EBO)
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void update(float deltaTime) {
        // Rotation animation is handled by the Scene class
    }

    @Override
    public void render() {
        // Bind the VAO and draw the pyramid
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_INT, 0); // 18 indices for 6 triangles
        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        // Delete the VBO, EBO and VAO
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vaoId);
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setRotationSpeed(float speed) {
        this.rotationSpeed = speed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setBaseScale(float scale) {
        this.baseScale = scale;
    }

    public float getBaseScale() {
        return baseScale;
    }
}
