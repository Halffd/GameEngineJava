package com.half;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

// JOML classes (will be used directly with fully qualified names to avoid import issues)
// import org.joml.Matrix4f;
// import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

// Shader class for handling OpenGL shaders
public class Shader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private Map<String, Integer> uniforms;

    public Shader() {
        programId = glCreateProgram();
        uniforms = new HashMap<>();
        if (programId == 0) {
            throw new RuntimeException("Could not create shader program");
        }
    }

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling shader: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);
        return shaderId;
    }

    public void link() {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking shader: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating shader: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, org.joml.Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(uniformName), false, buffer);
        }
    }

    public void setUniform(String uniformName, org.joml.Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    // Default shaders for basic rendering
    public static final String DEFAULT_VERTEX_SHADER = 
        "#version 330 core\n" +
        "\n" +
        "layout (location = 0) in vec3 position;\n" +
        "layout (location = 1) in vec3 color;\n" +
        "\n" +
        "uniform mat4 projectionMatrix;\n" +
        "uniform mat4 modelViewMatrix;\n" +
        "\n" +
        "out vec3 vertexColor;\n" +
        "\n" +
        "void main() {\n" +
        "    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);\n" +
        "    vertexColor = color;\n" +
        "}";

    public static final String DEFAULT_FRAGMENT_SHADER = 
        "#version 330 core\n" +
        "\n" +
        "in vec3 vertexColor;\n" +
        "uniform float time;\n" +
        "\n" +
        "out vec4 fragColor;\n" +
        "\n" +
        "void main() {\n" +
        "    // Animated color based on time\n" +
        "    float r = vertexColor.r * (sin(time) * 0.5 + 0.5);\n" +
        "    float g = vertexColor.g * (sin(time + 2.0) * 0.5 + 0.5);\n" +
        "    float b = vertexColor.b * (sin(time + 4.0) * 0.5 + 0.5);\n" +
        "    fragColor = vec4(r, g, b, 1.0);\n" +
        "}";
}

// Local Mesh class for shader testing purposes
class LocalMesh implements Renderable {
    private final Mesh mesh;
    private float[] vertices;
    private float[] colors;
    private int[] indices;

    public LocalMesh(float[] positions, float[] colors, int[] indices) {
        // Convert interleaved vertices for the main Mesh class
        this.vertices = positions;
        this.colors = colors;
        this.indices = indices;
        
        // Create a simple mesh for rendering
        float[] interleaved = new float[positions.length / 3 * 3]; // Just positions for now
        System.arraycopy(positions, 0, interleaved, 0, positions.length);
        this.mesh = new Mesh(interleaved);
    }

    @Override
    public void render() {
        mesh.render();
    }

    @Override
    public void cleanup() {
        if (mesh != null) {
            mesh.cleanup();
        }
    }
    
    @Override
    public void setColor(float r, float g, float b, float a) {
        mesh.setColor(r, g, b, a);
    }
}