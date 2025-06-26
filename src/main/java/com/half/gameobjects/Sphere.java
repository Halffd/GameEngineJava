package com.half.gameobjects;

import com.half.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class Sphere extends GameObject {
    private int vaoId;
    private int vboId;
    private int eboId;
    private int indexCount;
    private float radius;
    private Vector4f color;
    private float rotationSpeed;
    private float baseScale;

    public Sphere(String name, float radius, int sectorCount, int stackCount) {
        super(name);
        this.radius = radius;
        this.baseScale = 1.0f;
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.rotationSpeed = 1.0f;
        generateSphere(sectorCount, stackCount);
    }

    private void generateSphere(int sectorCount, int stackCount) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float x, y, z, xy;
        float nx, ny, nz, lengthInv = 1.0f / radius;
        float s, t;

        float sectorStep = 2 * (float)Math.PI / sectorCount;
        float stackStep = (float)Math.PI / stackCount;
        float sectorAngle, stackAngle;

        // Generate vertices and normals
        for (int i = 0; i <= stackCount; ++i) {
            stackAngle = (float)Math.PI / 2 - i * stackStep;
            xy = radius * (float)Math.cos(stackAngle);
            z = radius * (float)Math.sin(stackAngle);

            for (int j = 0; j <= sectorCount; ++j) {
                sectorAngle = j * sectorStep;

                // Vertex position
                x = xy * (float)Math.cos(sectorAngle);
                y = xy * (float)Math.sin(sectorAngle);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);

                // Normalized normal
                nx = x * lengthInv;
                ny = y * lengthInv;
                nz = z * lengthInv;
                vertices.add(nx);
                vertices.add(ny);
                vertices.add(nz);
            }
        }

        // Generate indices
        int k1, k2;
        for (int i = 0; i < stackCount; ++i) {
            k1 = i * (sectorCount + 1);
            k2 = k1 + sectorCount + 1;

            for (int j = 0; j < sectorCount; ++j, ++k1, ++k2) {
                if (i != 0) {
                    indices.add(k1);
                    indices.add(k2);
                    indices.add(k1 + 1);
                }

                if (i != (stackCount - 1)) {
                    indices.add(k1 + 1);
                    indices.add(k2);
                    indices.add(k2 + 1);
                }
            }
        }

        // Convert lists to arrays
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertexArray.length; i++) {
            vertexArray[i] = vertices.get(i);
        }

        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indexArray.length; i++) {
            indexArray[i] = indices.get(i);
        }
        this.indexCount = indexArray.length;

        // Create VAO and bind it
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO for vertices and normals
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer vertexBuffer = memAllocFloat(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Position attribute (location = 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Normal attribute (location = 1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Create EBO for indices
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        IntBuffer indexBuffer = memAllocInt(indexArray.length);
        indexBuffer.put(indexArray).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // Unbind VAO (and VBO + EBO)
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        // Free the memory
        memAllocFloat(vertexArray.length).clear();
        memAllocInt(indexArray.length).clear();
    }

    @Override
    public void update(float deltaTime) {
        // Rotation animation is handled by the Scene class
    }

    @Override
    public void render() {
        // Bind the VAO and draw the sphere
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
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
