# Minecraft Clone - Java

A Minecraft clone written in Java using OpenGL and LWJGL 3.

## Features

- ✅ 3D voxel-based world
- ✅ Procedurally generated terrain with 5x5 chunks
- ✅ First-person camera with mouse look
- ✅ Player movement (WASD) and jumping (SPACE)
- ✅ Multiple block types (Grass, Dirt, Stone, Wood, Leaves, Water, Sand)
- ✅ OpenGL 3.3 rendering with shaders
- ✅ Gravity and collision detection

## Requirements

- Java 11 or higher
- Maven 3.6+
- Windows, macOS, or Linux

## Building

```bash
mvn clean package
```

This will compile the project and create an executable JAR file in the `target/` directory.

## Running

```bash
mvn exec:java -Dexec.mainClass="com.minecraft.clone.Main"
```

Or run the packaged JAR:

```bash
java -jar target/minecraft-clone-1.0.0.jar
```

## Controls

- **W** - Move Forward
- **A** - Move Left
- **S** - Move Backward
- **D** - Move Right
- **SPACE** - Jump
- **Mouse** - Look Around
- **ESC** - Exit Game

## Project Structure

```
src/main/java/com/minecraft/clone/
├── Main.java                 # Entry point
├── engine/
│   └── Game.java            # Main game loop
├── graphics/
│   ├── Window.java          # GLFW window management
│   ├── Camera.java          # First-person camera
│   ├── Renderer.java        # Main renderer
│   ├── ShaderProgram.java   # GLSL shader wrapper
│   └── Mesh.java            # 3D mesh rendering
├── world/
│   ├── World.java           # World manager
│   ├── Chunk.java           # Terrain chunks
│   ├── Block.java           # Individual blocks
│   └── BlockType.java       # Block type enumeration
├── player/
│   └── Player.java          # Player character
└── input/
    └── InputHandler.java    # Keyboard and mouse input
```

## Technologies

- **LWJGL 3.3.1** - OpenGL and window management
- **JOML 1.10.5** - Math library for 3D graphics
- **SLF4J & Logback** - Logging framework
- **OpenGL 3.3** - Graphics API

## Performance Notes

- Renders a 5x5 grid of 16x16x256 chunks
- Targets 60 FPS
- Uses basic frustum culling (can be optimized)

## Future Improvements

- Block breaking and placement
- Inventory system
- Textures and better visuals
- More advanced terrain generation
- Entity system
- Multiplayer support
- Sound effects

## License

Apache License 2.0
