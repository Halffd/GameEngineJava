package com.half;

/**
 * PerformanceMonitor - tracks and reports performance metrics
 */
public class PerformanceMonitor {
    private long frameCount;
    private float totalTime;
    private float lastFpsUpdate;
    private int currentFps;
    private float frameTime;

    /**
     * Updates performance metrics
     * @param deltaTime Time since last update in seconds
     */
    public void update(float deltaTime) {
        frameCount++;
        totalTime += deltaTime;
        frameTime = deltaTime;
        
        // Update FPS every second
        if (totalTime - lastFpsUpdate >= 1.0f) {
            currentFps = (int)(frameCount / (totalTime - lastFpsUpdate));
            frameCount = 0;
            lastFpsUpdate = totalTime;
        }
    }

    public int getFps() {
        return currentFps;
    }

    public float getFrameTime() {
        return frameTime * 1000.0f; // Convert to milliseconds
    }
}
