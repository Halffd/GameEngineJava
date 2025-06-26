package com.half;


import java.util.stream.Collectors;

// Full Scene class with proper management
public class Scene {
    private List<GameObject> gameObjects;
    private List<GameObject> toAdd;
    private List<GameObject> toRemove;
    private Shader shader;
    private Camera camera;
    private Matrix4f modelMatrix;
    private Matrix4f modelViewMatrix;

    public Scene(int windowWidth, int windowHeight) {
        gameObjects = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
        camera = new Camera((float)windowWidth / windowHeight);
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();

        // Initialize default shader
        initializeShader();
    }

    private void initializeShader() {
        shader = new Shader();
        shader.createVertexShader(Shader.DEFAULT_VERTEX_SHADER);
        shader.createFragmentShader(Shader.DEFAULT_FRAGMENT_SHADER);
        shader.link();

        // Create uniforms
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("time");
    }

    public void addGameObject(GameObject gameObject) {
        toAdd.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        toRemove.add(gameObject);
    }

    public GameObject findGameObject(String name) {
        return gameObjects.stream()
                .filter(obj -> obj.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<GameObject> findGameObjectsWithTag(String tag) {
        // You'd implement a tagging system here
        return new ArrayList<>();
    }

    public void update(float deltaTime) {
        // Add new objects
        for (GameObject obj : toAdd) {
            gameObjects.add(obj);
            obj.start();
        }
        toAdd.clear();

        // Remove objects
        for (GameObject obj : toRemove) {
            gameObjects.remove(obj);
            obj.cleanup();
        }
        toRemove.clear();

        // Update all active objects
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                obj.update(deltaTime);
            }
        }

        // Check collisions (naive O(n²) - use spatial partitioning for real games)
        checkCollisions();
    }

    private void checkCollisions() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            if (!obj1.hasPhysics() || !obj1.isActive()) continue;

            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);
                if (!obj2.hasPhysics() || !obj2.isActive()) continue;

                if (obj1.checkCollision(obj2)) {
                    obj1.onCollision(obj2);
                    obj2.onCollision(obj1);
                }
            }
        }
    }

    public void render(float time) {
        shader.bind();

        // Set global uniforms
        shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
        shader.setUniform("time", time);

        // Render all active objects
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                // Create model matrix for this object
                modelMatrix.identity()
                        .translate(obj.getPosition())
                        .rotateX((float)Math.toRadians(obj.getRotation().x))
                        .rotateY((float)Math.toRadians(obj.getRotation().y))
                        .rotateZ((float)Math.toRadians(obj.getRotation().z))
                        .scale(obj.getScale());

                // Combine with view matrix
                modelViewMatrix.set(camera.getViewMatrix()).mul(modelMatrix);
                shader.setUniform("modelViewMatrix", modelViewMatrix);

                obj.render();
            }
        }

        shader.unbind();
    }

    public void cleanup() {
        for (GameObject obj : gameObjects) {
            obj.cleanup();
        }
        gameObjects.clear();

        if (shader != null) {
            shader.cleanup();
        }
    }

    // Utility methods
    public int getObjectCount() { return gameObjects.size(); }
    public Camera getCamera() { return camera; }
    public Shader getShader() { return shader; }

    // Window resize handling
    public void onWindowResize(int width, int height) {
        camera.setAspectRatio((float)width / height);
    }

    // Input handling (you'd expand this)
    public void onKeyPress(int key) {
        // Handle scene-level input
    }
    public String toString() {
        String objs = "";
        int objSize = 0;
        for (GameObject obj : gameObjects) {
            objs += obj.toString() + "\n";
            objSize++;
        }
        return "Scene{" +
                "gameObjectsSize=" + objSize +
                ", toAdd=" + toAdd +
                ", toRemove=" + toRemove +
                ", shader=" + shader +
                ", camera=" + camera +
                ", modelMatrix=" + modelMatrix +
                ", modelViewMatrix=" + modelViewMatrix +
                ", gameObjects= {" + objs + "}";
    }
}
