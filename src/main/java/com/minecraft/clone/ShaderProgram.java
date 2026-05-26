package com.minecraft.clone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public void createVertexShader(String source) {
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        if (vertexShaderId == 0) {
            throw new RuntimeException("Could not create vertex shader");
        }

        glShaderSource(vertexShaderId, source);
        glCompileShader(vertexShaderId);

        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(vertexShaderId, 1024));
            throw new RuntimeException("Could not compile vertex shader");
        }
    }

    public void createFragmentShader(String source) {
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0) {
            throw new RuntimeException("Could not create fragment shader");
        }

        glShaderSource(fragmentShaderId, source);
        glCompileShader(fragmentShaderId);

        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(fragmentShaderId, 1024));
            throw new RuntimeException("Could not compile fragment shader");
        }
    }

    public void link() {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create shader program");
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(programId, 1024));
            throw new RuntimeException("Could not link shader program");
        }

        glValidateProgram(programId);

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(programId, 1024));
            throw new RuntimeException("Could not validate shader program");
        }
    }

    public void bindAttribute(String name, int index) {
        glBindAttribLocation(programId, index, name);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getProgramId() {
        return programId;
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
        if (vertexShaderId != 0) {
            glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDeleteShader(fragmentShaderId);
        }
    }
}
