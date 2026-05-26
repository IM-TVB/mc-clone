package com.minecraft.clone;

import org.lwjgl.glfw.GLFW;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw;
    private float pitch;

    private float movementSpeed;
    private float mouseSensitivity;

    private int width;
    private int height;

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        front = new Vector3f(0.0f, 0.0f, -1.0f);
        up = new Vector3f(0.0f, 1.0f, 0.0f);
        right = new Vector3f(1.0f, 0.0f, 0.0f);
        worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
        yaw = -90.0f;
        pitch = 0.0f;
        movementSpeed = 5.0f;
        mouseSensitivity = 0.1f;

        updateCameraVectors();
        
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        
        updateProjectionMatrix();
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        updateCameraVectors();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        updateCameraVectors();
    }

    public void processKeyboard(int direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        
        if (direction == GLFW_KEY_W) {
            position.add(front.x * velocity, front.y * velocity, front.z * velocity);
        } else if (direction == GLFW_KEY_S) {
            position.sub(front.x * velocity, front.y * velocity, front.z * velocity);
        } else if (direction == GLFW_KEY_A) {
            position.sub(right.x * velocity, right.y * velocity, right.z * velocity);
        } else if (direction == GLFW_KEY_D) {
            position.add(right.x * velocity, right.y * velocity, right.z * velocity);
        } else if (direction == GLFW_KEY_SPACE) {
            position.add(0, velocity, 0);
        } else if (direction == GLFW_KEY_LEFT_SHIFT) {
            position.sub(0, velocity, 0);
        }
    }

    public void processMouseMovement(float xoffset, float yoffset) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f) {
            pitch = 89.0f;
        }
        if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        updateCameraVectors();
    }

    public void processMouseScroll(float yoffset) {
        movementSpeed -= yoffset;
        if (movementSpeed < 1.0f) {
            movementSpeed = 1.0f;
        }
        if (movementSpeed > 20.0f) {
            movementSpeed = 20.0f;
        }
    }

    private void updateCameraVectors() {
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.normalize();

        right.set(front.cross(worldUp)).normalize();
        up.set(right.cross(front)).normalize();
    }

    public void update() {
        updateProjectionMatrix();
        updateViewMatrix();
    }

    private void updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        float fov = (float) Math.toRadians(45.0f);
        float near = 0.1f;
        float far = 1000.0f;
        
        projectionMatrix.perspective(fov, aspectRatio, near, far);
    }

    private void updateViewMatrix() {
        viewMatrix.lookAt(position, new Vector3f(position).add(front), up);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }
}
