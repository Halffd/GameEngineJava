package com.half;

import static org.lwjgl.glfw.GLFW.*;

/**
 * InputManager - handles keyboard and mouse input
 */
public class InputManager {
    private final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private double mouseX, mouseY;
    private double lastMouseX, lastMouseY;
    private long window;

    /**
     * Initialize the input manager with the GLFW window
     */
    public void init(long window) {
        this.window = window;
        
        // Set up key callback
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW_RELEASE;
            }
        });
        
        // Set up mouse button callback
        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button >= 0 && button < mouseButtons.length) {
                mouseButtons[button] = action != GLFW_RELEASE;
            }
        });
        
        // Set up cursor position callback
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            mouseX = xpos;
            mouseY = ypos;
        });
    }
    
    /**
     * Check if a key is currently pressed
     */
    public boolean isKeyPressed(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }
    
    /**
     * Check if a mouse button is currently pressed
     */
    public boolean isMouseButtonPressed(int button) {
        return button >= 0 && button < mouseButtons.length && mouseButtons[button];
    }
    
    /**
     * Get current mouse X position
     */
    public double getMouseX() {
        return mouseX;
    }
    
    /**
     * Get current mouse Y position
     */
    public double getMouseY() {
        return mouseY;
    }
    
    /**
     * Get mouse X movement since last update
     */
    public double getMouseDeltaX() {
        return mouseX - lastMouseX;
    }
    
    /**
     * Get mouse Y movement since last update
     */
    public double getMouseDeltaY() {
        return mouseY - lastMouseY;
    }
}
