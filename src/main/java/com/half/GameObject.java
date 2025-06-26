package com.half;

// Full GameObject class with all the bells and whistles
public abstract class GameObject implements Renderable {
    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;
    protected Vector3f velocity;
    protected Mesh mesh;
    protected boolean active;
    protected String name;

    // Physics properties
    protected boolean hasPhysics;
    protected Vector3f bounds; // For collision detection

    public GameObject(String name, Mesh mesh) {
        this.name = name;
        this.mesh = mesh;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.velocity = new Vector3f(0, 0, 0);
        this.active = true;
        this.hasPhysics = false;
        this.bounds = new Vector3f(1, 1, 1); // Default unit cube bounds
    }

    // Core lifecycle methods
    public abstract void start(); // Called when object is added to scene
    public abstract void update(float deltaTime);
    public abstract void onCollision(GameObject other);

    // Physics update
    public void updatePhysics(float deltaTime) {
        if (!hasPhysics) return;

        // Apply velocity
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        position.z += velocity.z * deltaTime;
    }

    // Collision detection (AABB)
    public boolean checkCollision(GameObject other) {
        if (!hasPhysics || !other.hasPhysics) return false;

        Vector3f thisMin = new Vector3f(
                position.x - bounds.x * scale.x,
                position.y - bounds.y * scale.y,
                position.z - bounds.z * scale.z
        );
        Vector3f thisMax = new Vector3f(
                position.x + bounds.x * scale.x,
                position.y + bounds.y * scale.y,
                position.z + bounds.z * scale.z
        );

        Vector3f otherMin = new Vector3f(
                other.position.x - other.bounds.x * other.scale.x,
                other.position.y - other.bounds.y * other.scale.y,
                other.position.z - other.bounds.z * other.scale.z
        );
        Vector3f otherMax = new Vector3f(
                other.position.x + other.bounds.x * other.scale.x,
                other.position.y + other.bounds.y * other.scale.y,
                other.position.z + other.bounds.z * other.scale.z
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
    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) { this.position = position; }
    public void setPosition(float x, float y, float z) { this.position.set(x, y, z); }

    public Vector3f getRotation() { return rotation; }
    public void setRotation(Vector3f rotation) { this.rotation = rotation; }
    public void setRotation(float x, float y, float z) { this.rotation.set(x, y, z); }

    public Vector3f getScale() { return scale; }
    public void setScale(Vector3f scale) { this.scale = scale; }
    public void setScale(float x, float y, float z) { this.scale.set(x, y, z); }

    public Vector3f getVelocity() { return velocity; }
    public void setVelocity(Vector3f velocity) { this.velocity = velocity; }
    public void setVelocity(float x, float y, float z) { this.velocity.set(x, y, z); }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getName() { return name; }

    public boolean hasPhysics() { return hasPhysics; }
    public void setPhysics(boolean hasPhysics) { this.hasPhysics = hasPhysics; }

    public Vector3f getBounds() { return bounds; }
    public void setBounds(Vector3f bounds) { this.bounds = bounds; }
    public boolean equals(GameObject other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.hashCode() != this.hashCode()) return false;
        if (other.getClass() != this.getClass()) return false;
    }
    public String toString() {
        return "GameObject{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                ", velocity=" + velocity +
                ", active=" + active +
                ", hasPhysics=" + hasPhysics +
                ", bounds=" + bounds +
                ", hashCode=" + hashCode() +
                ", Mesh hashCode=" + mesh.hashCode() +
                '}';
    }
}
