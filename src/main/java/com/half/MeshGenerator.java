package com.half;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {

    public static float[] generateSphereVertices(float radius, int sectorCount, int stackCount) {
        List<Float> vertices = new ArrayList<>();

        float x, y, z, xy;
        float nx, ny, nz, lengthInv = 1.0f / radius;

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

        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertexArray.length; i++) {
            vertexArray[i] = vertices.get(i);
        }
        return vertexArray;
    }

    public static int[] generateSphereIndices(int sectorCount, int stackCount) {
        List<Integer> indices = new ArrayList<>();

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

        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indexArray.length; i++) {
            indexArray[i] = indices.get(i);
        }
        return indexArray;
    }

    public static float[] generateCubeVertices(float size) {
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

        // Create a new array for scaled vertices
        float[] scaledVertices = new float[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            scaledVertices[i] = vertices[i] * size;
        }
        return scaledVertices;
    }

    public static int[] generateCubeIndices() {
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
        return indices;
    }

    public static float[] generatePyramidVertices(float baseSize, float height) {
        // Pyramid vertices (positions and normals)
        // Base vertices (z = -height/2)
        float hs = baseSize * 0.5f;
        float hh = height * 0.5f;
        
        float[] vertices = {
            // Base (square)
            -hs, -hh, -hs,  0.0f, -1.0f,  0.0f,  // Bottom-left
             hs, -hh, -hs,  0.0f, -1.0f,  0.0f,  // Bottom-right
             hs, -hh,  hs,  0.0f, -1.0f,  0.0f,  // Top-right
            -hs, -hh,  hs,  0.0f, -1.0f,  0.0f,  // Top-left
            
            // Apex
             0.0f,  hh,  0.0f,  0.0f,  0.0f,  1.0f  // Top point
        };
        return vertices;
    }

    public static int[] generatePyramidIndices() {
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
        return indices;
    }

    public static float[] generatePrismVertices(float width, float height, float depth) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;

        float[] vertices = {
            // Front face (triangle)
            -hw, -hh,  hd,  0.0f,  0.0f,  1.0f, // Bottom-left
             hw, -hh,  hd,  0.0f,  0.0f,  1.0f, // Bottom-right
             0.0f,  hh,  hd,  0.0f,  0.0f,  1.0f, // Top-center

            // Back face (triangle)
            -hw, -hh, -hd,  0.0f,  0.0f, -1.0f, // Bottom-left
             hw, -hh, -hd,  0.0f,  0.0f, -1.0f, // Bottom-right
             0.0f,  hh, -hd,  0.0f,  0.0f, -1.0f, // Top-center

            // Left face (quad)
            -hw, -hh, -hd, -1.0f,  0.0f,  0.0f,
            -hw, -hh,  hd, -1.0f,  0.0f,  0.0f,
             0.0f,  hh,  hd, -1.0f,  0.0f,  0.0f,
             0.0f,  hh, -hd, -1.0f,  0.0f,  0.0f,

            // Right face (quad)
             hw, -hh,  hd,  1.0f,  0.0f,  0.0f,
             hw, -hh, -hd,  1.0f,  0.0f,  0.0f,
             0.0f,  hh, -hd,  1.0f,  0.0f,  0.0f,
             0.0f,  hh,  hd,  1.0f,  0.0f,  0.0f,

            // Bottom face (quad)
            -hw, -hh, -hd,  0.0f, -1.0f,  0.0f,
             hw, -hh, -hd,  0.0f, -1.0f,  0.0f,
             hw, -hh,  hd,  0.0f, -1.0f,  0.0f,
            -hw, -hh,  hd,  0.0f, -1.0f,  0.0f
        };
        return vertices;
    }

    public static int[] generatePrismIndices() {
        int[] indices = {
            // Front
            0, 1, 2,
            // Back
            3, 5, 4,
            // Left
            6, 7, 8, 8, 9, 6,
            // Right
            10, 11, 12, 12, 13, 10,
            // Bottom
            14, 15, 16, 16, 17, 14
        };
        return indices;
    }
}
