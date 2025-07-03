package com.half;

import com.half.MeshGenerator;

public class SphereMesh extends Mesh {

    public SphereMesh(float radius, int sectorCount, int stackCount) {
        super(MeshGenerator.generateSphereVertices(radius, sectorCount, stackCount), MeshGenerator.generateSphereIndices(sectorCount, stackCount));
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
