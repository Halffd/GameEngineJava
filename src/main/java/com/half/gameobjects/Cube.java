package com.half.gameobjects;

import com.half.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Cube extends GameObject {
    private int vaoId;
    private int vboId;
    private int eboId;
    private float size;
    private Vector4f color;
    private float rotationSpeed;
    private float baseScale;

    public Cube(String name, float size) {
        super(name);
        this.size = size;
        this.baseScale = 1.0f;
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.rotationSpeed = 1.0f;
        init();
    }

    private void init() {
        // Cube vertices (positions and normals)
        float[] vertices = {
            // Front face
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f, // Bottom-left
             0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f, // Bottom-right
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f, // Top-right
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f, // Top-left
            // Back face
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, // Bottom-left
             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, // Bottom-right
             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, // Top-right
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f  // Top-left
        };

        // Indices for drawing the cube
        int[] indices = {
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

        // Scale vertices by size
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= size;
        }

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
        // Bind the VAO and draw the cube
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
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
