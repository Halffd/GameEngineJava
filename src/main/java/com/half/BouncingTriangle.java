package com.half;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BouncingTriangle {
    private long window;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Triangle properties
    private float x = WIDTH / 2.0f;
    private float y = HEIGHT / 2.0f;
    private float velX = 200.0f; // pixels per second
    private float velY = 150.0f;
    private float size = 50.0f;

    // Color cycling
    private float colorTime = 0.0f;
    private long lastTime = System.nanoTime();

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
// LWJGL doesn't care about instruction set
// It only cares about pointer size for native interop
        boolean is64Bit = System.getProperty("os.arch").contains("64");
        System.out.println("is64Bit: " + is64Bit);
        if (!is64Bit) {
            System.err.println("Warning: 32-bit JVM detected, using 64-bit LWJGL natives.");
        }
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "DVD Bouncing Triangle", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;

            update(deltaTime);
            render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void update(float deltaTime) {
        // Update position
        x += velX * deltaTime;
        y += velY * deltaTime;

        // Get current window size for dynamic bouncing
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, width, height);

            int w = width.get(0);
            int h = height.get(0);

            // Bounce off walls
            if (x - size <= 0 || x + size >= w) {
                velX = -velX;
                x = Math.max(size, Math.min(w - size, x));
            }
            if (y - size <= 0 || y + size >= h) {
                velY = -velY;
                y = Math.max(size, Math.min(h - size, y));
            }
        }

        // Update color cycling
        colorTime += deltaTime * 2.0f; // Speed of color change
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        // Get window dimensions for proper coordinate system
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, width, height);

            int w = width.get(0);
            int h = height.get(0);

            glViewport(0, 0, w, h);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, w, h, 0, -1, 1); // Flip Y axis
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        }

        // Draw gradient triangle
        glBegin(GL_TRIANGLES);

        // Top vertex - Red cycling
        float r1 = (float) (Math.sin(colorTime) * 0.5 + 0.5);
        float g1 = (float) (Math.sin(colorTime + 2.0) * 0.5 + 0.5);
        float b1 = (float) (Math.sin(colorTime + 4.0) * 0.5 + 0.5);
        glColor3f(r1, g1, b1);
        glVertex2f(x, y - size);

        // Bottom left vertex - Green cycling
        float r2 = (float) (Math.sin(colorTime + 1.0) * 0.5 + 0.5);
        float g2 = (float) (Math.sin(colorTime + 3.0) * 0.5 + 0.5);
        float b2 = (float) (Math.sin(colorTime + 5.0) * 0.5 + 0.5);
        glColor3f(r2, g2, b2);
        glVertex2f(x - size * 0.866f, y + size * 0.5f);

        // Bottom right vertex - Blue cycling
        float r3 = (float) (Math.sin(colorTime + 2.0) * 0.5 + 0.5);
        float g3 = (float) (Math.sin(colorTime + 4.0) * 0.5 + 0.5);
        float b3 = (float) (Math.sin(colorTime + 6.0) * 0.5 + 0.5);
        glColor3f(r3, g3, b3);
        glVertex2f(x + size * 0.866f, y + size * 0.5f);

        glEnd();
    }

    private void cleanup() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}