package com.half.gameobjects;

import com.half.Mesh;
import com.half.MeshGenerator;

public class PrismMesh extends Mesh {

    public PrismMesh(float width, float height, float depth) {
        super(MeshGenerator.generatePrismVertices(width, height, depth), MeshGenerator.generatePrismIndices());
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
