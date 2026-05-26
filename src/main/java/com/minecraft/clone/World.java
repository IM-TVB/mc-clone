package com.minecraft.clone;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class World {

    private static final int CHUNK_SIZE = 16;
    private static final int WORLD_HEIGHT = 64;
    
    private byte[][][] blocks;
    
    private int vao;
    private int vbo;
    private int vertexCount;

    public World() {
        blocks = new byte[CHUNK_SIZE][WORLD_HEIGHT][CHUNK_SIZE];
    }

    public void generate() {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < WORLD_HEIGHT; y++) {
                    if (y == 0) {
                        blocks[x][y][z] = 1; // Bedrock
                    } else if (y < 10) {
                        blocks[x][y][z] = 2; // Stone
                    } else if (y < 20) {
                        blocks[x][y][z] = 3; // Dirt
                    } else if (y == 20) {
                        blocks[x][y][z] = 4; // Grass
                    } else if (y > 20 && y < 25) {
                        if (Math.random() < 0.1) {
                            blocks[x][y][z] = 5; // Tree trunk
                        }
                    } else if (y >= 25 && y < 30) {
                        if (blocks[x][y-1][z] == 5) {
                            blocks[x][y][z] = 6; // Leaves
                        }
                    }
                }
            }
        }
        
        generateMesh();
    }

    private void generateMesh() {
        List<Float> vertices = new ArrayList<>();
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    byte block = blocks[x][y][z];
                    if (block != 0) {
                        addBlockVertices(vertices, x, y, z, block);
                    }
                }
            }
        }
        
        vertexCount = vertices.size() / 6;
        
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size());
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip();
        
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
        glEnableVertexAttribArray(0);
        
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
        glEnableVertexAttribArray(1);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void addBlockVertices(List<Float> vertices, int x, int y, int z, byte blockType) {
        float[] color = getBlockColor(blockType);
        
        // Only render faces that are exposed to air
        boolean top = isAir(x, y + 1, z);
        boolean bottom = isAir(x, y - 1, z);
        boolean left = isAir(x - 1, y, z);
        boolean right = isAir(x + 1, y, z);
        boolean front = isAir(x, y, z + 1);
        boolean back = isAir(x, y, z - 1);
        
        if (top) {
            // Top face
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2]);
        }
        
        if (bottom) {
            // Bottom face
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.5f); vertices.add(color[1] * 0.5f); vertices.add(color[2] * 0.5f);
        }
        
        if (left) {
            // Left face
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
        }
        
        if (right) {
            // Right face
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.8f); vertices.add(color[1] * 0.8f); vertices.add(color[2] * 0.8f);
        }
        
        if (front) {
            // Front face
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z + 1);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
        }
        
        if (back) {
            // Back face
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x + 1); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x); vertices.add((float) y + 1); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x + 1); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
            
            vertices.add((float) x); vertices.add((float) y); vertices.add((float) z);
            vertices.add(color[0] * 0.7f); vertices.add(color[1] * 0.7f); vertices.add(color[2] * 0.7f);
        }
    }

    private boolean isAir(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= WORLD_HEIGHT || z < 0 || z >= CHUNK_SIZE) {
            return true;
        }
        return blocks[x][y][z] == 0;
    }

    private float[] getBlockColor(byte blockType) {
        switch (blockType) {
            case 1: return new float[]{0.4f, 0.4f, 0.4f}; // Bedrock
            case 2: return new float[]{0.5f, 0.5f, 0.5f}; // Stone
            case 3: return new float[]{0.6f, 0.4f, 0.2f}; // Dirt
            case 4: return new float[]{0.3f, 0.8f, 0.2f}; // Grass
            case 5: return new float[]{0.5f, 0.3f, 0.1f}; // Tree trunk
            case 6: return new float[]{0.2f, 0.6f, 0.1f}; // Leaves
            default: return new float[]{1.0f, 1.0f, 1.0f};
        }
    }

    public void render() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    public void cleanup() {
        if (vbo != 0) {
            glDeleteBuffers(vbo);
        }
        if (vao != 0) {
            glDeleteVertexArrays(vao);
        }
    }
}
