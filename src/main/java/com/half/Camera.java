package com.half;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Camera {
    // Camera vectors
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;
    
    // Euler angles
    private float yaw = -90f;
    private float pitch = 0f;
    
    // Camera options
    private float movementSpeed = 5.0f;
    private float mouseSensitivity = 0.1f;
    private float fov = 45.0f;
    
    // Projection
    private float aspectRatio;
    private float nearPlane = 0.1f;
    private float farPlane = 1000.0f;
    
    // Input
    private boolean firstMouse = true;
    private float lastX = 400;
    private float lastY = 300;

    public Camera(float aspectRatio) {
        this.position = new Vector3f(0.0f, 0.0f, 3.0f);
        this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.aspectRatio = aspectRatio;
        updateCameraVectors();
    }
    
    public void processKeyboard(int key, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        
        if (key == GLFW.GLFW_KEY_W) {
            position.add(front.mul(velocity, new Vector3f()));
        }
        if (key == GLFW.GLFW_KEY_S) {
            position.sub(front.mul(velocity, new Vector3f()));
        }
        if (key == GLFW.GLFW_KEY_A) {
            position.sub(right.mul(velocity, new Vector3f()));
        }
        if (key == GLFW.GLFW_KEY_D) {
            position.add(right.mul(velocity, new Vector3f()));
        }
        if (key == GLFW.GLFW_KEY_SPACE) {
            position.add(0, velocity, 0);
        }
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
            position.sub(0, velocity, 0);
        }
    }
    
    public void processMouseMovement(float xpos, float ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }
        
        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos; // Reversed since y-coordinates go from bottom to top
        lastX = xpos;
        lastY = ypos;
        
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;
        
        yaw += xoffset;
        pitch += yoffset;
        
        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;
        
        updateCameraVectors();
    }
    
    public void processMouseScroll(float yoffset) {
        if (fov >= 1.0f && fov <= 45.0f) fov -= yoffset;
        if (fov <= 1.0f) fov = 1.0f;
        if (fov >= 45.0f) fov = 45.0f;
    }
    
    private void updateCameraVectors() {
        // Calculate the new front vector
        Vector3f newFront = new Vector3f();
        newFront.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        newFront.y = (float) Math.sin(Math.toRadians(pitch));
        newFront.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front = newFront.normalize();
        
        // Also re-calculate the right and up vector
        this.right = new Vector3f(front).cross(worldUp).normalize();
        this.up = new Vector3f(right).cross(front).normalize();
    }
    
    public Matrix4f getViewMatrix() {
        Vector3f center = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, center, up);
    }
    
    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective((float)Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
    }
    
    // Getters and setters
    public Vector3f getPosition() { return position; }
    public Vector3f getFront() { return front; }
    public Vector3f getUp() { return up; }
    public Vector3f getRight() { return right; }
    public float getFov() { return fov; }
    
    public void setAspectRatio(float aspectRatio) { 
        this.aspectRatio = aspectRatio; 
    }
    
    public void setPosition(Vector3f position) { 
        this.position = position; 
    }
}
