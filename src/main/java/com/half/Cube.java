package com.half;

// Enhanced Cube implementation
public class Cube extends GameObject {
    private static final float[] CUBE_POSITIONS = {
            // Front face
            -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,
            // Back face
            -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f,
            // Left face
            -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f,
            // Right face
            0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,
            // Top face
            -0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,
            // Bottom face
            -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f
    };

    private static final float[] CUBE_COLORS = {
            // Each vertex gets RGB values - rainbow cube
            0.0f, 1.0f, 1.0f, 0.0f, 0.5f, 1.0f, // Back
            0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.5f, // Left
            1.0f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Right
            0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, // Top
            0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f  // Bottom
    };

    private static final int[] CUBE_INDICES = {
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 6, 5, 4, 7, 6,
            // Left face
            8, 9, 10, 8, 10, 11,
            // Right face
            12, 14, 13, 12, 15, 14,
            // Top face
            16, 17, 18, 16, 18, 19,
            // Bottom face
            20, 22, 21, 20, 23, 22
    };

    private float rotationSpeed;
    private float colorTime;

    public Cube(String name) {
        super(name, new Mesh(CUBE_POSITIONS, CUBE_COLORS, CUBE_INDICES));
        this.rotationSpeed = 45.0f; // degrees per second
        this.colorTime = 0.0f;
        this.hasPhysics = true;
        this.bounds = new Vector3f(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void start() {
        System.out.println("Cube " + name + " started at position " + position.x + ", " + position.y + ", " + position.z);
    }

    @Override
    public void update(float deltaTime) {
        // Rotate the cube
        rotation.y += rotationSpeed * deltaTime;
        rotation.x += rotationSpeed * 0.5f * deltaTime;

        // Update color animation time
        colorTime += deltaTime;

        // Update physics
        updatePhysics(deltaTime);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println(name + " collided with " + other.getName());
        // Simple bounce effect
        velocity.x *= -0.8f;
        velocity.y *= -0.8f;
        velocity.z *= -0.8f;
    }

    public float getColorTime() { return colorTime; }
    public void setRotationSpeed(float speed) { this.rotationSpeed = speed; }
}

// Updated Main class with proper modern OpenGL
public class Main {
    private long window;
    private Scene scene;
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
        // GLFW setup
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // OpenGL 3.3 Core Profile
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Modern OpenGL Game Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Input callbacks
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            if (scene != null) {
                scene.onKeyPress(key);
            }
        });

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
            if (scene != null) {
                scene.onWindowResize(width, height);
            }
        });

        // Center window
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
        glfwSwapInterval(1); // V-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        // OpenGL setup
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Initialize scene
        setupScene();

        lastTime = System.nanoTime();
        totalTime = 0.0f;
    }

    private void setupScene() {
        scene = new Scene(WIDTH, HEIGHT);

        // Create some cubes with physics
        Cube cube1 = new Cube("BouncyCube1");
        cube1.setPosition(-2, 0, 0);
        cube1.setVelocity(1, 0.5f, 0);
        cube1.setRotationSpeed(30);

        Cube cube2 = new Cube("BouncyCube2");
        cube2.setPosition(2, 0, 0);
        cube2.setVelocity(-1, -0.3f, 0);
        cube2.setRotationSpeed(60);

        Cube cube3 = new Cube("StaticCube");
        cube3.setPosition(0, 3, 0);
        cube3.setScale(0.5f, 0.5f, 0.5f);
        cube3.setRotationSpeed(120);

        scene.addGameObject(cube1);
        scene.addGameObject(cube2);
        scene.addGameObject(cube3);
        System.out.println(scene);

        // Set up camera
        scene.getCamera().setPosition(new Vector3f(0, 0, 8));
        scene.getCamera().setTarget(new Vector3f(0, 0, 0));
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;
            totalTime += deltaTime;

            // Update scene
            scene.update(deltaTime);

            // Render
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            scene.render(totalTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
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
        new Main().run();
    }
}

// Bonus: Advanced GameObject types for more functionality

// BouncingCube - extends Cube with screen boundary bouncing
public class BouncingCube extends Cube {
    private Vector3f screenBounds;
    private float bounceReduction;

    public BouncingCube(String name, Vector3f screenBounds) {
        super(name);
        this.screenBounds = screenBounds;
        this.bounceReduction = 0.9f; // Energy loss on bounce
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Check screen boundaries and bounce
        if (position.x - bounds.x <= -screenBounds.x || position.x + bounds.x >= screenBounds.x) {
            velocity.x *= -bounceReduction;
            position.x = Math.max(-screenBounds.x + bounds.x, Math.min(screenBounds.x - bounds.x, position.x));
        }

        if (position.y - bounds.y <= -screenBounds.y || position.y + bounds.y >= screenBounds.y) {
            velocity.y *= -bounceReduction;
            position.y = Math.max(-screenBounds.y + bounds.y, Math.min(screenBounds.y - bounds.y, position.y));
        }

        if (position.z - bounds.z <= -screenBounds.z || position.z + bounds.z >= screenBounds.z) {
            velocity.z *= -bounceReduction;
            position.z = Math.max(-screenBounds.z + bounds.z, Math.min(screenBounds.z - bounds.z, position.z));
        }
    }
}

// GameObjectFactory - because creating objects manually is tedious
public class GameObjectFactory {

    public static Cube createCube(String name, Vector3f position, Vector3f scale) {
        Cube cube = new Cube(name);
        cube.setPosition(position);
        cube.setScale(scale);
        return cube;
    }

    public static BouncingCube createBouncingCube(String name, Vector3f position, Vector3f velocity, Vector3f bounds) {
        BouncingCube cube = new BouncingCube(name, bounds);
        cube.setPosition(position);
        cube.setVelocity(velocity);
        return cube;
    }

    public static GameObject createSphere(String name) {
        // You'd implement sphere mesh generation here
        // For now, just return a scaled cube
        Cube sphere = new Cube(name);
        sphere.setScale(0.8f, 0.8f, 0.8f);
        return sphere;
    }
}

// Performance Monitor - because you need to know when things go to shit
public class PerformanceMonitor {
    private long frameCount;
    private float totalTime;
    private float lastFpsUpdate;
    private int currentFps;
    private float frameTime;

    public void update(float deltaTime) {
        frameCount++;
        totalTime += deltaTime;
        frameTime = deltaTime * 1000; // Convert to milliseconds

        if (totalTime - lastFpsUpdate >= 1.0f) {
            currentFps = (int)(frameCount / (totalTime - lastFpsUpdate));
            lastFpsUpdate = totalTime;
            frameCount = 0;

            // Print performance info
            System.out.printf("FPS: %d, Frame Time: %.2fms%n", currentFps, frameTime);
        }
    }

    public int getFps() { return currentFps; }
    public float getFrameTime() { return frameTime; }
}

// Input Manager - because handling input properly is important
public class InputManager {
    private boolean[] keys = new boolean[GLFW_KEY_LAST];
    private boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private double mouseX, mouseY;
    private double lastMouseX, lastMouseY;

    public void init(long window) {
        glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW_RELEASE;
            }
        });

        glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            if (button >= 0 && button < mouseButtons.length) {
                mouseButtons[button] = action != GLFW_RELEASE;
            }
        });

        glfwSetCursorPosCallback(window, (window1, xpos, ypos) -> {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            mouseX = xpos;
            mouseY = ypos;
        });
    }

    public boolean isKeyPressed(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }

    public boolean isMouseButtonPressed(int button) {
        return button >= 0 && button < mouseButtons.length && mouseButtons[button];
    }

    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public double getMouseDeltaX() { return mouseX - lastMouseX; }
    public double getMouseDeltaY() { return mouseY - lastMouseY; }
}