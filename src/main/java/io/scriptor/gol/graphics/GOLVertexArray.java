package io.scriptor.gol.graphics;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GOLVertexArray {

    private final int mPtr;

    public GOLVertexArray() {
        mPtr = glGenVertexArrays();
    }

    public GOLVertexArray bind() {
        glBindVertexArray(mPtr);
        return this;
    }

    public void unbind() {
        glBindVertexArray(0);
    }
}
