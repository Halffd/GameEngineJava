package com.half;

import com.half.MeshGenerator;

public class CubeMesh extends Mesh {

    public CubeMesh(float size) {
        super(MeshGenerator.generateCubeVertices(size), MeshGenerator.generateCubeIndices());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}