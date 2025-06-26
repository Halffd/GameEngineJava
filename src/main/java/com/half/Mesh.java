package com.half;
// Base Renderable interface
public interface Renderable {
    void render();
    void cleanup();
}

// Mesh class - handles VAO/VBO management
public class Mesh implements Renderable {
    private int vaoId;
    private int vboId;
    private int vertexCount;

    public Mesh(float[] vertices) {
        this.vertexCount = vertices.length / 3; // Assuming 3D vertices

        // Generate and bind VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Generate and bind VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Configure vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // Unbind
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void render() {
        glBindVertexArray(vaoId);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }
}

// Scene manager to handle multiple objects
public class Scene {
    private List<GameObject> objects;

    public Scene() {
        objects = new ArrayList<>();
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void update(float deltaTime) {
        for (GameObject obj : objects) {
            obj.update(deltaTime);
        }
    }

    public void render() {
        for (GameObject obj : objects) {
            obj.render();
        }
    }

    public void cleanup() {
        for (GameObject obj : objects) {
            obj.cleanup();
        }
    }
}

// Updated Main class
public class Main {
    private long window;
    private Scene scene;
    private long lastTime;

    // Your existing init code...

    private void init() {
        // Your existing GLFW/OpenGL setup...

        // Create scene and add objects
        scene = new Scene();

        Cube cube1 = new Cube();
        cube1.setPosition(new Vector3f(100, 100, 0));

        Cube cube2 = new Cube();
        cube2.setPosition(new Vector3f(300, 200, 0));

        scene.addObject(cube1);
        scene.addObject(cube2);

        lastTime = System.nanoTime();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;

            scene.update(deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            scene.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        scene.cleanup();
        // Your existing cleanup...
    }
}