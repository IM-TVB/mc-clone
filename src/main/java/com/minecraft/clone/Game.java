package com.minecraft.clone;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Game {

    private int width;
    private int height;
    
    private Camera camera;
    private World world;
    private ShaderProgram shader;
    
    private int vao;
    private int vbo;
    
    private float rotation = 0.0f;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        
        initOpenGL();
        initCamera();
        initWorld();
        initShader();
    }

    private void initOpenGL() {
        glClearColor(0.4f, 0.6f, 0.9f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    private void initCamera() {
        camera = new Camera(width, height);
        camera.setPosition(0.0f, 5.0f, 10.0f);
    }

    private void initWorld() {
        world = new World();
        world.generate();
    }

    private void initShader() {
        shader = new ShaderProgram();
        
        String vertexShaderSource = 
            "#version 150\n" +
            "in vec3 in_position;\n" +
            "in vec3 in_color;\n" +
            "out vec3 ex_color;\n" +
            "uniform mat4 projection_matrix;\n" +
            "uniform mat4 view_matrix;\n" +
            "uniform mat4 model_matrix;\n" +
            "void main(void) {\n" +
            "    gl_Position = projection_matrix * view_matrix * model_matrix * vec4(in_position, 1.0);\n" +
            "    ex_color = in_color;\n" +
            "}\n";
        
        String fragmentShaderSource = 
            "#version 150\n" +
            "in vec3 ex_color;\n" +
            "out vec4 out_color;\n" +
            "void main(void) {\n" +
            "    out_color = vec4(ex_color, 1.0);\n" +
            "}\n";
        
        shader.createVertexShader(vertexShaderSource);
        shader.createFragmentShader(fragmentShaderSource);
        shader.link();
        
        shader.bindAttribute("in_position", 0);
        shader.bindAttribute("in_color", 1);
    }

    public void update() {
        camera.update();
        rotation += 0.5f;
    }

    public void render() {
        shader.bind();
        
        Matrix4f projectionMatrix = camera.getProjectionMatrix();
        Matrix4f viewMatrix = camera.getViewMatrix();
        
        int projectionLocation = glGetUniformLocation(shader.getProgramId(), "projection_matrix");
        int viewLocation = glGetUniformLocation(shader.getProgramId(), "view_matrix");
        int modelLocation = glGetUniformLocation(shader.getProgramId(), "model_matrix");
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(projectionLocation, false, projectionMatrix.get(stack.mallocFloat(16)));
            glUniformMatrix4fv(viewLocation, false, viewMatrix.get(stack.mallocFloat(16)));
        }
        
        Matrix4f modelMatrix = new Matrix4f().rotateY((float) Math.toRadians(rotation));
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(modelLocation, false, modelMatrix.get(stack.mallocFloat(16)));
        }
        
        world.render();
        
        shader.unbind();
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
        world.cleanup();
    }
}
