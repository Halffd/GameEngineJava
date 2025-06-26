package com.half;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    // Constants
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;
    private static final String WINDOW_TITLE = "3D Scene with Colorful Objects";
    private static final int MAX_OBJECTS = 100;
    private static final float MOUSE_SENSITIVITY = 0.05f;

    // Window and graphics
    private long window;
    private int width = INITIAL_WIDTH;
    private int height = INITIAL_HEIGHT;
    private Scene scene;
    private Camera camera;

    // Input handling
    private double lastX, lastY;
    private boolean firstMouse = true;
    private boolean[] keys = new boolean[GLFW_KEY_LAST];

    // Timing
    private double lastTime = 0.0;
    private double deltaTime = 0.0;
    private int frameCount = 0;
    private double lastFpsTime = 0.0;

    // State
    private boolean running = true;
    private final Random random = new Random();

    // Objects management
    private List<ColorfulObject> objects = new ArrayList<>();
    private float objectSpawnCooldown = 0.0f;
    private static final float SPAWN_COOLDOWN_TIME = 0.5f; // seconds

    public static void main(String[] args) {
        System.out.println("Starting 3D Colorful Scene Application...");
        try {
            new Main().run();
        } catch (Exception e) {
            System.err.println("Fatal error occurred:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            System.err.println("Error in main loop:");
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void init() {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window
        window = glfwCreateWindow(width, height, WINDOW_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup input callbacks
        setupInputCallbacks();

        // Center window on screen
        centerWindow();

        // Make OpenGL context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);

        // Initialize OpenGL
        GL.createCapabilities();

        // Set OpenGL state
        glClearColor(0.05f, 0.05f, 0.15f, 1.0f); // Dark blue background
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Initialize scene and camera
        initializeScene();

        // Create initial objects
        createInitialObjects();

        // Print controls
        printControls();

        // Initialize timing
        lastTime = glfwGetTime();
        lastFpsTime = lastTime;
    }

    private void setupInputCallbacks() {
        // Key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW_RELEASE;
            }

            // Toggle cursor mode with TAB
            if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
                toggleCursorMode();
            }
        });

        // Mouse position callback
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if (glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
                handleMouseMovement(xpos, ypos);
            }
        });

        // Mouse scroll callback
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            if (glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED && camera != null) {
                camera.processMouseScroll((float) yoffset);
            }
        });

        // Window resize callback
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;
            glViewport(0, 0, w, h);
            if (camera != null) {
                camera.setAspectRatio((float) w / h);
            }
        });

        // Start with cursor disabled (camera mode)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private void centerWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidmode != null) {
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }
    }

    private void initializeScene() {
        try {
            camera = new Camera((float) width / height);
            if (camera != null) {
                camera.setPosition(new org.joml.Vector3f(0, 0, 5));
            }
        } catch (Exception e) {
            System.err.println("Error initializing camera: " + e.getMessage());
            // Create a fallback camera setup
            camera = new Camera(1.0f);
        }
    }

    private void createInitialObjects() {
        // Create various colorful objects
        for (int i = 0; i < 20; i++) {
            createRandomObject();
        }
    }

    private void createRandomObject() {
        if (objects.size() >= MAX_OBJECTS) {
            return; // Prevent memory issues
        }

        try {
            // Random position
            float x = (random.nextFloat() - 0.5f) * 20.0f;
            float y = (random.nextFloat() - 0.5f) * 10.0f;
            float z = -5.0f - random.nextFloat() * 15.0f;

            // Random size
            float size = 0.3f + random.nextFloat() * 1.0f;

            // Random color
            float r = 0.3f + random.nextFloat() * 0.7f;
            float g = 0.3f + random.nextFloat() * 0.7f;
            float b = 0.3f + random.nextFloat() * 0.7f;

            // Random rotation speed
            float rotSpeed = 0.5f + random.nextFloat() * 2.0f;

            // Create object
            ColorfulObject obj = new ColorfulObject(
                    "Object_" + System.currentTimeMillis() + "_" + random.nextInt(1000),
                    x, y, z, size, r, g, b, rotSpeed
            );

            objects.add(obj);
        } catch (Exception e) {
            System.err.println("Error creating random object: " + e.getMessage());
        }
    }

    private void gameLoop() {
        while (!glfwWindowShouldClose(window) && running) {
            try {
                // Update timing
                double currentTime = glfwGetTime();
                deltaTime = currentTime - lastTime;
                lastTime = currentTime;

                // Update FPS counter
                updateFpsCounter(currentTime);

                // Handle input
                handleInput();

                // Update scene
                update((float) deltaTime);

                // Render
                render();

                // Swap buffers and poll events
                glfwSwapBuffers(window);
                glfwPollEvents();

            } catch (Exception e) {
                System.err.println("Error in game loop:");
                e.printStackTrace();
                running = false;
            }
        }
    }

    private void updateFpsCounter(double currentTime) {
        frameCount++;
        if (currentTime - lastFpsTime >= 1.0) {
            System.out.println("FPS: " + frameCount + " | Objects: " + objects.size());
            frameCount = 0;
            lastFpsTime = currentTime;
        }
    }

    private void handleInput() {
        // Exit on ESC
        if (keys[GLFW_KEY_ESCAPE]) {
            glfwSetWindowShouldClose(window, true);
        }

        // Camera movement using GLFW key constants
        if (camera != null && glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
            float speed = 5.0f * (float) deltaTime;

            if (keys[GLFW_KEY_W]) camera.processKeyboard(GLFW_KEY_W, speed);
            if (keys[GLFW_KEY_S]) camera.processKeyboard(GLFW_KEY_S, speed);
            if (keys[GLFW_KEY_A]) camera.processKeyboard(GLFW_KEY_A, speed);
            if (keys[GLFW_KEY_D]) camera.processKeyboard(GLFW_KEY_D, speed);
            if (keys[GLFW_KEY_SPACE]) camera.processKeyboard(GLFW_KEY_SPACE, speed);
            if (keys[GLFW_KEY_LEFT_SHIFT]) camera.processKeyboard(GLFW_KEY_LEFT_SHIFT, speed);
        }

        // Spawn objects
        objectSpawnCooldown -= (float) deltaTime;
        if (keys[GLFW_KEY_C] && objectSpawnCooldown <= 0) {
            createRandomObject();
            objectSpawnCooldown = SPAWN_COOLDOWN_TIME;
        }

        // Clear objects
        if (keys[GLFW_KEY_X]) {
            clearObjects();
        }
    }

    private void handleMouseMovement(double xpos, double ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        float xoffset = (float) (xpos - lastX) * MOUSE_SENSITIVITY;
        float yoffset = (float) (lastY - ypos) * MOUSE_SENSITIVITY;
        lastX = xpos;
        lastY = ypos;

        if (camera != null) {
            camera.processMouseMovement(xoffset, yoffset);
        }
    }

    private void toggleCursorMode() {
        int cursorMode = glfwGetInputMode(window, GLFW_CURSOR);
        if (cursorMode == GLFW_CURSOR_DISABLED) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            firstMouse = true;
        }
    }

    private void update(float deltaTime) {
        // Update all objects
        for (ColorfulObject obj : objects) {
            if (obj != null) {
                obj.update(deltaTime);
            }
        }
    }

    private void render() {
        // Clear screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set up 3D projection if camera is available
        if (camera != null) {
            // Set up projection matrix
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            float fov = camera.getFov();
            float aspect = (float) width / height;
            float nearPlane = 0.1f;
            float farPlane = 1000.0f;

            // Simple perspective projection
            float top = (float) (nearPlane * Math.tan(Math.toRadians(fov / 2.0)));
            float bottom = -top;
            float right = top * aspect;
            float left = -right;

            glFrustum(left, right, bottom, top, nearPlane, farPlane);

            // Set up model-view matrix
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            // Apply camera transformation
            org.joml.Vector3f pos = camera.getPosition();
            org.joml.Vector3f front = camera.getFront();
            org.joml.Vector3f up = camera.getUp();

            // Look at target
            float targetX = pos.x + front.x;
            float targetY = pos.y + front.y;
            float targetZ = pos.z + front.z;

            gluLookAt(pos.x, pos.y, pos.z, targetX, targetY, targetZ, up.x, up.y, up.z);
        }

        // Render objects
        renderObjects();
    }

    private void renderObjects() {
        // Simple object rendering
        for (ColorfulObject obj : objects) {
            if (obj != null) {
                obj.render();
            }
        }
    }

    private void clearObjects() {
        objects.clear();
        System.out.println("Cleared all objects");
    }

    private void printControls() {
        System.out.println("\n=== CONTROLS ===");
        System.out.println("WASD: Move camera");
        System.out.println("SPACE: Move up");
        System.out.println("LEFT SHIFT: Move down");
        System.out.println("MOUSE: Look around");
        System.out.println("C: Create new object");
        System.out.println("X: Clear all objects");
        System.out.println("TAB: Toggle cursor mode");
        System.out.println("ESC: Exit");
        System.out.println("================\n");
    }

    private void cleanup() {
        running = false;

        try {
            // Clean up objects
            for (ColorfulObject obj : objects) {
                if (obj != null) {
                    obj.cleanup();
                }
            }
            objects.clear();

            // Clean up GLFW
            if (window != 0) {
                glfwFreeCallbacks(window);
                glfwDestroyWindow(window);
            }

            GLFWErrorCallback callback = glfwSetErrorCallback(null);
            if (callback != null) {
                callback.free();
            }
            glfwTerminate();

            System.out.println("Cleanup completed successfully");
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // GLU LookAt implementation since it might not be available
    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ) {
        // Forward vector
        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;

        // Normalize forward
        float flen = (float) Math.sqrt(fx*fx + fy*fy + fz*fz);
        if (flen != 0) {
            fx /= flen;
            fy /= flen;
            fz /= flen;
        }

        // Right vector (forward cross up)
        float rx = fy * upZ - fz * upY;
        float ry = fz * upX - fx * upZ;
        float rz = fx * upY - fy * upX;

        // Normalize right
        float rlen = (float) Math.sqrt(rx*rx + ry*ry + rz*rz);
        if (rlen != 0) {
            rx /= rlen;
            ry /= rlen;
            rz /= rlen;
        }

        // Up vector (right cross forward)
        float ux = ry * fz - rz * fy;
        float uy = rz * fx - rx * fz;
        float uz = rx * fy - ry * fx;

        // Create transformation matrix
        float[] m = {
                rx, ux, -fx, 0,
                ry, uy, -fy, 0,
                rz, uz, -fz, 0,
                0, 0, 0, 1
        };

        glMultMatrixf(m);
        glTranslatef(-eyeX, -eyeY, -eyeZ);
    }

    // Simple colorful object class
    private static class ColorfulObject {
        private String name;
        private float x, y, z, size;
        private float r, g, b;
        private float rotationSpeed;
        private float rotation = 0;
        private float time = 0;
        private float bobOffset;

        public ColorfulObject(String name, float x, float y, float z, float size,
                              float r, float g, float b, float rotSpeed) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
            this.rotationSpeed = rotSpeed;
            this.bobOffset = (float) (Math.random() * Math.PI * 2); // Random phase for bobbing
        }

        public void update(float deltaTime) {
            rotation += rotationSpeed * deltaTime * 50; // degrees
            time += deltaTime;

            // Color animation with different frequencies
            float baseR = r;
            float baseG = g;
            float baseB = b;

            r = Math.max(0.1f, Math.min(1.0f, baseR * (float)(Math.sin(time * 1.5 + bobOffset) * 0.4 + 0.8)));
            g = Math.max(0.1f, Math.min(1.0f, baseG * (float)(Math.sin(time * 2.0 + bobOffset + 2) * 0.4 + 0.8)));
            b = Math.max(0.1f, Math.min(1.0f, baseB * (float)(Math.sin(time * 1.2 + bobOffset + 4) * 0.4 + 0.8)));

            // Gentle bobbing motion
            y += (float)(Math.sin(time * 2 + bobOffset) * 0.01);
        }

        public void render() {
            glPushMatrix();
            glTranslatef(x, y, z);
            glRotatef(rotation, 1, 1, 0.5f);
            glColor3f(r, g, b);

            // Draw a colorful cube with gradient faces
            glBegin(GL_QUADS);

            // Front face (brighter)
            glColor3f(r * 1.2f, g * 1.2f, b * 1.2f);
            glVertex3f(-size, -size, size);
            glVertex3f(size, -size, size);
            glVertex3f(size, size, size);
            glVertex3f(-size, size, size);

            // Back face (darker)
            glColor3f(r * 0.6f, g * 0.6f, b * 0.6f);
            glVertex3f(-size, -size, -size);
            glVertex3f(-size, size, -size);
            glVertex3f(size, size, -size);
            glVertex3f(size, -size, -size);

            // Top face
            glColor3f(r * 1.0f, g * 1.0f, b * 1.0f);
            glVertex3f(-size, size, -size);
            glVertex3f(-size, size, size);
            glVertex3f(size, size, size);
            glVertex3f(size, size, -size);

            // Bottom face
            glColor3f(r * 0.8f, g * 0.8f, b * 0.8f);
            glVertex3f(-size, -size, -size);
            glVertex3f(size, -size, -size);
            glVertex3f(size, -size, size);
            glVertex3f(-size, -size, size);

            // Right face
            glColor3f(r * 0.9f, g * 0.9f, b * 0.9f);
            glVertex3f(size, -size, -size);
            glVertex3f(size, size, -size);
            glVertex3f(size, size, size);
            glVertex3f(size, -size, size);

            // Left face
            glColor3f(r * 0.7f, g * 0.7f, b * 0.7f);
            glVertex3f(-size, -size, -size);
            glVertex3f(-size, -size, size);
            glVertex3f(-size, size, size);
            glVertex3f(-size, size, -size);

            glEnd();
            glPopMatrix();
        }

        public void cleanup() {
            // Nothing to cleanup for this simple object
        }
    }
}