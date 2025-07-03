package com.half;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Base class for all game objects in the scene.
 * Handles common properties like position, rotation, scale, and physics.
 */
public abstract class GameObject implements Renderable {
    protected Transform transform;
    protected Vector3f velocity;
    protected Mesh mesh;
    protected boolean active;
    protected String name;
    protected float rotationSpeed;
    protected Vector4f color;

    // Physics properties
    protected boolean hasPhysics;
    protected Vector3f bounds; // For collision detection

    public GameObject(String name, Mesh mesh) {
        this.name = name;
        this.mesh = mesh;
        this.transform = new Transform();
        this.velocity = new Vector3f(0, 0, 0);
        this.active = true;
        this.hasPhysics = false;
        this.bounds = new Vector3f(1, 1, 1); // Default unit cube bounds
        this.rotationSpeed = 0.0f;
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f); // Default to white
    }

    // Core lifecycle methods
    public abstract void start(); // Called when object is added to scene
    public abstract void update(float deltaTime);
    public abstract void onCollision(GameObject other);

    // Physics update
    public void updatePhysics(float deltaTime) {
        if (!hasPhysics) return;

        // Apply velocity
        transform.getPosition().x += velocity.x * deltaTime;
        transform.getPosition().y += velocity.y * deltaTime;
        transform.getPosition().z += velocity.z * deltaTime;
    }

    // Collision detection (AABB)
    public boolean checkCollision(GameObject other) {
        if (!hasPhysics || !other.hasPhysics) return false;

        Vector3f thisMin = new Vector3f(
                transform.getPosition().x - bounds.x * transform.getScale().x,
                transform.getPosition().y - bounds.y * transform.getScale().y,
                transform.getPosition().z - bounds.z * transform.getScale().z
        );
        Vector3f thisMax = new Vector3f(
                transform.getPosition().x + bounds.x * transform.getScale().x,
                transform.getPosition().y + bounds.y * transform.getScale().y,
                transform.getPosition().z + bounds.z * transform.getScale().z
        );

        Vector3f otherMin = new Vector3f(
                other.transform.getPosition().x - other.bounds.x * other.transform.getScale().x,
                other.transform.getPosition().y - other.bounds.y * other.transform.getScale().y,
                other.transform.getPosition().z - other.bounds.z * other.transform.getScale().z
        );
        Vector3f otherMax = new Vector3f(
                other.transform.getPosition().x + other.bounds.x * other.transform.getScale().x,
                other.transform.getPosition().y + other.bounds.y * other.transform.getScale().y,
                other.transform.getPosition().z + other.bounds.z * other.transform.getScale().z
        );

        return (thisMin.x <= otherMax.x && thisMax.x >= otherMin.x) &&
                (thisMin.y <= otherMax.y && thisMax.y >= otherMin.y) &&
                (thisMin.z <= otherMax.z && thisMax.z >= otherMin.z);
    }

    @Override
    public void render() {
        if (!active || mesh == null) return;
        mesh.render();
    }

    @Override
    public void cleanup() {
        if (mesh != null) {
            mesh.cleanup();
        }
    }

    // Getters and setters
    public Transform getTransform() { return transform; }

    public Vector3f getPosition() { return transform.getPosition(); }
    public void setPosition(Vector3f position) { this.transform.setPosition(position); }
    public void setPosition(float x, float y, float z) { this.transform.setPosition(x, y, z); }

    public Vector3f getRotation() { return transform.getRotation(); }
    public void setRotation(Vector3f rotation) { this.transform.setRotation(rotation); }
    public void setRotation(float x, float y, float z) { this.transform.setRotation(x, y, z); }

    public Vector3f getScale() { return transform.getScale(); }
    public void setScale(Vector3f scale) { this.transform.setScale(scale); }
    public void setScale(float x, float y, float z) { this.transform.setScale(x, y, z); }

    public Vector3f getVelocity() { return velocity; }
    
    public void setVelocity(Vector3f velocity) { this.velocity = velocity; }
    public void setVelocity(float x, float y, float z) { this.velocity.set(x, y, z); }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isVisible() { return active; } // Assuming active means visible for now
    
    public boolean hasPhysics() { return hasPhysics; }
    public void setHasPhysics(boolean hasPhysics) { this.hasPhysics = hasPhysics; }
    
    public Vector3f getBounds() { return bounds; }
    public void setBounds(Vector3f bounds) { this.bounds = bounds; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Mesh getMesh() { return mesh; }
    public void setMesh(Mesh mesh) { this.mesh = mesh; }

    public float getRotationSpeed() { return rotationSpeed; }
    public void setRotationSpeed(float rotationSpeed) { this.rotationSpeed = rotationSpeed; }

    public Vector4f getColor() { return color; }
    @Override
    public void setColor(float r, float g, float b, float a) { this.color.set(r, g, b, a); }
    
    @Override
    public String toString() {
        return "GameObject{" +
                "name='" + name + "'" +
                ", transform=" + transform +
                ", velocity=" + velocity +
                ", active=" + active +
                ", hasPhysics=" + hasPhysics +
                ", bounds=" + bounds +
                ", rotationSpeed=" + rotationSpeed +
                ", color=" + color +
                ", hashCode=" + hashCode() +
                ", Mesh hashCode=" + (mesh != null ? mesh.hashCode() : "null") +
                "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameObject other = (GameObject) obj;
        return active == other.active && 
               hasPhysics == other.hasPhysics &&
               Float.compare(rotationSpeed, other.rotationSpeed) == 0 &&
               name.equals(other.name) &&
               transform.equals(other.transform) &&
               velocity.equals(other.velocity) &&
               bounds.equals(other.bounds) &&
               color.equals(other.color) &&
               (mesh == other.mesh || (mesh != null && mesh.equals(other.mesh)));
    }
    
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (transform != null ? transform.hashCode() : 0);
        result = 31 * result + (velocity != null ? velocity.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (hasPhysics ? 1 : 0);
        result = 31 * result + Float.hashCode(rotationSpeed);
        result = 31 * result + (bounds != null ? bounds.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (mesh != null ? mesh.hashCode() : 0);
        return result;
    }
}
