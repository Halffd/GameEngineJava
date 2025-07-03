package com.half;

import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Mesh class that handles VAO/VBO management for rendering 3D objects.
 * Implements Renderable and AutoCloseable interfaces for resource management.
 */
public class Mesh implements Renderable, AutoCloseable {
    protected int vaoId;
    protected int vboId;
    protected int eboId;
    protected int vertexCount;
    protected float[] vertices;
    private boolean initialized = false;
    private final Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f); // Default white color
    
    // Track if this mesh has been closed
    private volatile boolean closed = false;

    public Mesh(float[] vertices, int[] indices) {
        if (vertices == null || vertices.length == 0) {
            throw new IllegalArgumentException("Vertices array cannot be null or empty");
        }
        if (indices == null || indices.length == 0) {
            throw new IllegalArgumentException("Indices array cannot be null or empty");
        }

        this.vertices = vertices;
        this.vertexCount = indices.length; // Number of indices, not vertices
        FloatBuffer vertexBuffer = null;
        IntBuffer indexBuffer = null;

        try {
            // Create a new FloatBuffer for vertices
            vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();

            // Create a new IntBuffer for indices
            indexBuffer = MemoryUtil.memAllocInt(indices.length);
            indexBuffer.put(indices).flip();

            // Generate and bind VAO
            vaoId = glGenVertexArrays();
            if (vaoId == 0) {
                throw new RuntimeException("Failed to create VAO");
            }
            glBindVertexArray(vaoId);

            // Generate and bind VBO
            vboId = glGenBuffers();
            if (vboId == 0) {
                throw new RuntimeException("Failed to create VBO");
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            // Configure vertex attributes (position and normal)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            // Generate and bind EBO
            eboId = glGenBuffers();
            if (eboId == 0) {
                throw new RuntimeException("Failed to create EBO");
            }
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

            // Unbind
            glBindVertexArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            
            initialized = true;
        } finally {
            // Free the memory if buffers were created
            if (vertexBuffer != null) {
                MemoryUtil.memFree(vertexBuffer);
            }
            if (indexBuffer != null) {
                MemoryUtil.memFree(indexBuffer);
            }
        }
    }

    @Override
    public void render() {
        if (!initialized) {
            throw new IllegalStateException("Mesh not properly initialized");
        }
        
        try {
            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        } finally {
            glBindVertexArray(0);
        }
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }
    
    @Override
    public void cleanup() {
        if (closed) {
            return;
        }
        if (initialized) {
            if (vboId != 0) {
                glDeleteBuffers(vboId);
                vboId = 0;
            }
            if (vaoId != 0) {
                glDeleteVertexArrays(vaoId);
                vaoId = 0;
            }
            initialized = false;
        }
    }
    
    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     */
    @Override
    public void close() {
        if (!closed) {
            cleanup();
            closed = true;
        }
    }
    
    /**
     * Checks if this mesh has been closed.
     * @return true if the mesh has been closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    public int getVaoId() { return vaoId; }
    public int getVboId() { return vboId; }
    public int getEboId() { return eboId; }
    public int getVertexCount() { return vertexCount; }
    public float[] getVertices() { return vertices; }
}