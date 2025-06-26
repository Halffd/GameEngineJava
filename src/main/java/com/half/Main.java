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

public class Main {
    private long window;
    private Scene scene;
    private InputManager inputManager;
    private PerformanceMonitor performanceMonitor;
    private long lastTime;
    private float totalTime;

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Previous GLFW/OpenGL setup code...
        initWindow();

        // Initialize managers
        inputManager = new InputManager();
        inputManager.init(window);
        performanceMonitor = new PerformanceMonitor();

        // Setup enhanced scene
        setupEnhancedScene();

        lastTime = System.nanoTime();
        totalTime = 0.0f;
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Enhanced OpenGL Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        // Center and show window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
    }

    private void setupEnhancedScene() {
        scene = new Scene(WIDTH, HEIGHT);

        Vector3f sceneBounds = new Vector3f(8, 6, 5);

        // Create a bunch of bouncing cubes
        for (int i = 0; i < 5; i++) {
            BouncingCube cube = GameObjectFactory.createBouncingCube(
                    "Cube" + i,
                    new Vector3f(
                            (float)(Math.random() - 0.5) * 4,
                            (float)(Math.random() - 0.5) * 4,
                            (float)(Math.random() - 0.5) * 2
                    ),
                    new Vector3f(
                            (float)(Math.random() - 0.5) * 4,
                            (float)(Math.random() - 0.5) * 4,
                            (float)(Math.random() - 0.5) * 2
                    ),
                    sceneBounds
            );

            cube.setRotationSpeed((float)(Math.random() * 180 + 30));
            scene.addGameObject(cube);
        }

        // Set camera position
        scene.getCamera().setPosition(new Vector3f(0, 0, 12));
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;
            totalTime += deltaTime;

            // Handle input
            handleInput();

            // Update
            scene.update(deltaTime);
            performanceMonitor.update(deltaTime);

            // Render
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            scene.render(totalTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleInput() {
        Camera camera = scene.getCamera();
        float speed = 5.0f;

        // WASD camera movement
        if (inputManager.isKeyPressed(GLFW_KEY_W)) {
            camera.getPosition().z -= speed * 0.016f; // Approximate 60fps
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            camera.getPosition().z += speed * 0.016f;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            camera.getPosition().x -= speed * 0.016f;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            camera.getPosition().x += speed * 0.016f;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_Q)) {
            camera.getPosition().y += speed * 0.016f;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_E)) {
            camera.getPosition().y -= speed * 0.016f;
        }

        // Spawn new cube on space
        if (inputManager.isKeyPressed(GLFW_KEY_SPACE)) {
            spawnRandomCube();
        }

        // ESC to quit
        if (inputManager.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private void spawnRandomCube() {
        if (scene.getObjectCount() < 20) { // Limit to prevent chaos
            BouncingCube newCube = GameObjectFactory.createBouncingCube(
                    "SpawnedCube" + System.currentTimeMillis(),
                    new Vector3f(0, 0, 0),
                    new Vector3f(
                            (float)(Math.random() - 0.5) * 6,
                            (float)(Math.random() - 0.5) * 6,
                            (float)(Math.random() - 0.5) * 4
                    ),
                    new Vector3f(8, 6, 5)
            );

            newCube.setRotationSpeed((float)(Math.random() * 360));
            scene.addGameObject(newCube);
        }
    }

    private void cleanup() {
        if (scene != null) {
            scene.cleanup();
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new EnhancedMain().run();
    }
}