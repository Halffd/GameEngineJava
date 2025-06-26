package com.half;

public interface Renderable {
    void render();
    void cleanup();
    void setColor(float r, float g, float b, float a);
}
