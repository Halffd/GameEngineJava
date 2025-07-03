package com.half.gameobjects;

import com.half.Mesh;
import com.half.MeshGenerator;

public class PyramidMesh extends Mesh {

    public PyramidMesh(float baseSize, float height) {
        super(MeshGenerator.generatePyramidVertices(baseSize, height), MeshGenerator.generatePyramidIndices());
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