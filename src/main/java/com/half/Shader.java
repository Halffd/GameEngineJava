package com.half;
// Vector3f utility class (or use JOML library)
public class Vector3f {
    public float x, y, z;

    public Vector3f() { this(0, 0, 0); }
    public Vector3f(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }

    public Vector3f add(Vector3f other) { return new Vector3f(x + other.x, y + other.y, z + other.z); }
    public Vector3f mul(float scalar) { return new Vector3f(x * scalar, y * scalar, z * scalar); }
    public void set(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
}

// Shader class - because immediate mode is for cavemen
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

    public void setUniform(String uniformName, Matrix4f value) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        glUniformMatrix4fv(uniforms.get(uniformName), false, buffer);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    // Default shaders for basic rendering
    public static final String DEFAULT_VERTEX_SHADER = """
        #version 330 core
        
        layout (location = 0) in vec3 position;
        layout (location = 1) in vec3 color;
        
        uniform mat4 projectionMatrix;
        uniform mat4 modelViewMatrix;
        
        out vec3 vertexColor;
        
        void main() {
            gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
            vertexColor = color;
        }
        """;

    public static final String DEFAULT_FRAGMENT_SHADER = """
        #version 330 core
        
        in vec3 vertexColor;
        uniform float time;
        
        out vec4 fragColor;
        
        void main() {
            // Animated color based on time
            float r = vertexColor.r * (sin(time) * 0.5 + 0.5);
            float g = vertexColor.g * (sin(time + 2.0) * 0.5 + 0.5);
            float b = vertexColor.b * (sin(time + 4.0) * 0.5 + 0.5);
            fragColor = vec4(r, g, b, 1.0);
        }
        """;
}

// Enhanced Mesh class with colors
public class Mesh implements Renderable {
    private int vaoId;
    private int positionVboId;
    private int colorVboId;
    private int indexVboId;
    private int vertexCount;

    public Mesh(float[] positions, float[] colors, int[] indices) {
        vertexCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Position VBO
        positionVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // Color VBO
        colorVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        // Index VBO
        indexVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    @Override
    public void render() {
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(colorVboId);
        glDeleteBuffers(indexVboId);
        glDeleteVertexArrays(vaoId);
    }
}