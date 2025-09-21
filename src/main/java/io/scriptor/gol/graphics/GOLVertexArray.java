package io.scriptor.gol.graphics;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GOLVertexArray {

    private final int mPtr;

    public GOLVertexArray() {
        mPtr = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(mPtr);
    }

    public void unbind() {
        glBindVertexArray(0);
    }
}
