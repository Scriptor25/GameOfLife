package io.scriptor.gol.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class GOLProgram {

    private final int mPtr;
    private final Map<String, Integer> mUniformLocs = new HashMap<>();

    public GOLProgram(String... shaderfiles) throws IOException {
        mPtr = glCreateProgram();
        for (final var filename : shaderfiles) {
            final var ending = filename.substring(filename.lastIndexOf('.') + 1);
            final var type = switch (ending) {
                case "vsh" -> GL_VERTEX_SHADER;
                case "fsh" -> GL_FRAGMENT_SHADER;
                default -> throw new RuntimeException();
            };
            final var builder = new StringBuilder();
            final var reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename)));
            for (String line = reader.readLine(); line != null; line = reader.readLine())
                builder.append(line).append('\n');

            final var shader = glCreateShader(type);
            glShaderSource(shader, builder.toString());
            glCompileShader(shader);
            glAttachShader(mPtr, shader);
            glDeleteShader(shader);
        }

        glLinkProgram(mPtr);
        glValidateProgram(mPtr);
    }

    public GOLProgram bind() {
        glUseProgram(mPtr);
        return this;
    }

    public void unbind() {
        glUseProgram(0);
    }

    @FunctionalInterface
    public interface IUniform {
        void set(int loc);
    }

    public GOLProgram uniform(String name, IUniform uniform) {
        var loc = mUniformLocs.get(name);
        if (loc == null) {
            loc = glGetUniformLocation(mPtr, name);
            mUniformLocs.put(name, loc);
        }

        uniform.set(loc);
        return this;
    }
}
