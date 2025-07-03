package com.half;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private List<GameObject> gameObjects;
    private List<GameObject> toAdd;
    private List<GameObject> toRemove;
    private Shader shader;
    private Camera camera;
    private Matrix4f modelMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private float time = 0;
    private Random random = new Random();
    private int windowWidth;
    private int windowHeight;

    public Scene(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        
        gameObjects = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
        camera = new Camera((float)windowWidth / windowHeight);
        camera.setPosition(new Vector3f(0, 0, 5));
        
        modelMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();

        initializeShader();
        createRandomObjects(50); // Create 50 random objects
    }
    
    private void createRandomObjects(int count) {
        for (int i = 0; i < count; i++) {
            // Random position between -10 and 10 on all axes
            float x = (random.nextFloat() - 0.5f) * 20f;
            float y = (random.nextFloat() - 0.5f) * 20f;
            float z = (random.nextFloat() - 0.5f) * 20f;
            
            // Random size between 0.5 and 2.0
            float size = 0.5f + random.nextFloat() * 1.5f;
            
            // Random color
            float r = 0.2f + random.nextFloat() * 0.8f;
            float g = 0.2f + random.nextFloat() * 0.8f;
            float b = 0.2f + random.nextFloat() * 0.8f;
            
            // Random rotation speed
            float rotSpeed = 0.5f + random.nextFloat() * 2.0f;
            
            // Randomly choose between cube, sphere, and pyramid
            GameObject obj;
            int type = random.nextInt(3);
            
            switch (type) {
                case 0:
                    obj = new Cube("Cube_" + i, size);
                    break;
                case 1:
                    obj = new Sphere("Sphere_" + i, size, 20, 20);
                    break;
                case 2:
                default:
                    obj = new Pyramid("Pyramid_" + i, size, size * 2);
                    break;
            }
            
            // Set object properties
            obj.getTransform().setPosition(x, y, z);
            obj.setRotationSpeed(rotSpeed);
            obj.setColor(r, g, b, 1.0f);
            
            // Add to scene
            addGameObject(obj);
        }
    }

    private void initializeShader() {
        shader = new Shader();
        
        // Vertex shader with lighting
        String vertexShaderSource = "#version 330 core\n" +
            "layout (location = 0) in vec3 position;\n" +
            "layout (location = 1) in vec3 normal;\n" +
            "\n" +
            "uniform mat4 projectionMatrix;\n" +
            "uniform mat4 viewMatrix;\n" +
            "uniform mat4 modelMatrix;\n" +
            "\n" +
            "out vec3 FragPos;\n" +
            "out vec3 Normal;\n" +
            "\n" +
            "void main() {\n" +
            "    FragPos = vec3(modelMatrix * vec4(position, 1.0));\n" +
            "    Normal = mat3(transpose(inverse(modelMatrix))) * normal;\n" +
            "    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);\n" +
            "}";
                
        // Fragment shader with Phong lighting
        String fragmentShaderSource = "#version 330 core\n" +
            "in vec3 FragPos;\n" +
            "in vec3 Normal;\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "uniform vec3 viewPos;\n" +
            "uniform vec3 objectColor;\n" +
            "uniform float time;\n" +
            "\n" +
            "void main() {\n" +
            "    // Light properties\n" +
            "    vec3 lightPos = vec3(5.0 * sin(time * 0.5), 5.0, 5.0 * cos(time * 0.5));\n" +
            "    vec3 lightColor = vec3(1.0, 1.0, 1.0);\n" +
            "    \n" +
            "    // Ambient\n" +
            "    float ambientStrength = 0.2;\n" +
            "    vec3 ambient = ambientStrength * lightColor;\n" +
            "    \n" +
            "    // Diffuse\n" +
            "    vec3 norm = normalize(Normal);\n" +
            "    vec3 lightDir = normalize(lightPos - FragPos);\n" +
            "    float diff = max(dot(norm, lightDir), 0.0);\n" +
            "    vec3 diffuse = diff * lightColor;\n" +
            "    \n" +
            "    // Specular\n" +
            "    float specularStrength = 0.5;\n" +
            "    vec3 viewDir = normalize(viewPos - FragPos);\n" +
            "    vec3 reflectDir = reflect(-lightDir, norm);\n" +
            "    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);\n" +
            "    vec3 specular = specularStrength * spec * lightColor;\n" +
            "    \n" +
            "    // Combine results\n" +
            "    vec3 result = (ambient + diffuse + specular) * objectColor;\n" +
            "    FragColor = vec4(result, 1.0);\n" +
            "}";
        
        shader.createVertexShader(vertexShaderSource);
        shader.createFragmentShader(fragmentShaderSource);
        shader.link();

        // Create uniforms
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewPos");
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

    }
    
    public void render() {
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
        
        // Update view and projection matrices
        viewMatrix.set(camera.getViewMatrix());
        projectionMatrix.set(camera.getProjectionMatrix());
        
        // Use our shader program
        shader.bind();
        
        // Set shader uniforms
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("viewPos", camera.getPosition());
        shader.setUniform("time", time);
        
        // Render all game objects
        for (GameObject obj : gameObjects) {
            // Skip if object is not visible
            if (!obj.isVisible()) continue;
            
            // Update model matrix for this object
            modelMatrix.identity()
                .translate(obj.getTransform().getPosition())
                .rotateX((float)Math.toRadians(obj.getTransform().getRotation().x))
                .rotateY((float)Math.toRadians(obj.getTransform().getRotation().y))
                .rotateZ((float)Math.toRadians(obj.getTransform().getRotation().z))
                .scale(obj.getTransform().getScale());
            
            // Set model matrix and object color
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("objectColor", obj.getColor());
            
            // Render the object
            obj.render();
        }
        
        // Unbind shader
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
                
                ", gameObjects= {" + objs + "}";
    }
}
