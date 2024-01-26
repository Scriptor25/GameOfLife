package io.scriptor.gol.scene;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class GOLVertex {

    public static final int BYTES = Float.BYTES * (3 + 4);

    public final Vector3f position = new Vector3f();
    public final Vector4f color = new Vector4f(1.0f);

    public GOLVertex(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public GOLVertex(float x, float y, float a) {
        position.x = x;
        position.y = y;

        color.w = a;
    }

    public GOLVertex(float x, float y, float r, float g, float b) {
        position.x = x;
        position.y = y;

        color.x = r;
        color.y = g;
        color.z = b;
    }

    public GOLVertex(float x, float y, float r, float g, float b, float a) {
        position.x = x;
        position.y = y;

        color.x = r;
        color.y = g;
        color.z = b;
        color.w = a;
    }

    @Override
    public String toString() {
        return String.format("Position: %s, Color: %s", position, color);
    }

    public void copyTo(ByteBuffer buffer) {
        buffer
                .putFloat(position.x()).putFloat(position.y()).putFloat(position.z())
                .putFloat(color.x()).putFloat(color.y()).putFloat(color.z()).putFloat(color.w());
    }
}
