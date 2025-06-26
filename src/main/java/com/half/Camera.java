package com.half;


// Camera class for proper 3D viewing
public class Camera {
    private Vector3f position;
    private Vector3f target;
    private Vector3f up;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public Camera(float aspectRatio) {
        this.position = new Vector3f(0, 0, 5);
        this.target = new Vector3f(0, 0, 0);
        this.up = new Vector3f(0, 1, 0);
        this.fov = 45.0f;
        this.aspectRatio = aspectRatio;
        this.nearPlane = 0.1f;
        this.farPlane = 1000.0f;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, target, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective((float)Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
    }

    // Getters/setters
    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) { this.position = position; }
    public Vector3f getTarget() { return target; }
    public void setTarget(Vector3f target) { this.target = target; }
    public void setAspectRatio(float aspectRatio) { this.aspectRatio = aspectRatio; }
}
