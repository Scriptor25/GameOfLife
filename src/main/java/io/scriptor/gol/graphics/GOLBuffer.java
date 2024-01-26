package io.scriptor.gol.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class GOLBuffer {

    private final int mPtr;
    private final int mTarget;
    private final int mUsage;

    public GOLBuffer(int target, int usage) {
        mPtr = glGenBuffers();
        mTarget = target;
        mUsage = usage;
    }

    public GOLBuffer bind() {
        glBindBuffer(mTarget, mPtr);
        return this;
    }

    public void unbind() {
        glBindBuffer(mTarget, 0);
    }

    public GOLBuffer setData(float[] data) {
        glBufferData(mTarget, data, mUsage);
        return this;
    }

    public GOLBuffer setData(int[] data) {
        glBufferData(mTarget, data, mUsage);
        return this;
    }

    public GOLBuffer setData(ByteBuffer data) {
        glBufferData(mTarget, data, mUsage);
        return this;
    }

    public GOLBuffer setData(IntBuffer data) {
        glBufferData(mTarget, data, mUsage);
        return this;
    }
}
