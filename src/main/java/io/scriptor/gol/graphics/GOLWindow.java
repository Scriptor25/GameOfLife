package io.scriptor.gol.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GOLWindow {

    public static void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new RuntimeException();
    }

    public static void terminate() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private final long mPtr;
    private final List<GLFWKeyCallbackI> mKeyCallbacks = new Vector<>();
    private final List<GLFWScrollCallbackI> mScrollCallbacks = new Vector<>();

    public GOLWindow(int width, int height, String title) {
        glfwDefaultWindowHints();
        mPtr = glfwCreateWindow(width, height, title, NULL, NULL);
        if (mPtr == NULL)
            throw new RuntimeException();

        glfwSetKeyCallback(mPtr, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);

            for (final var callback : mKeyCallbacks)
                callback.invoke(window, key, scancode, action, mods);
        });
        glfwSetScrollCallback(mPtr, (window, xoffset, yoffset) -> {
            for (final var callback : mScrollCallbacks)
                callback.invoke(window, xoffset, yoffset);
        });

        glfwMakeContextCurrent(mPtr);
    }

    public boolean spin() {
        glfwSwapBuffers(mPtr);
        glfwPollEvents();

        return !glfwWindowShouldClose(mPtr);
    }

    public int[] getSize() {
        int[] w = new int[1], h = new int[1];
        glfwGetWindowSize(mPtr, w, h);
        return new int[]{w[0], h[0]};
    }

    public void register(GLFWKeyCallbackI callback) {
        mKeyCallbacks.add(callback);
    }

    public void register(GLFWScrollCallbackI callback) {
        mScrollCallbacks.add(callback);
    }

    public boolean getKey(int key) {
        return glfwGetKey(mPtr, key) == GLFW_PRESS;
    }

    public boolean getMouseButton(int button) {
        return glfwGetMouseButton(mPtr, button) == GLFW_PRESS;
    }

    public float[] getMousePos() {
        double[] x = new double[1], y = new double[1];
        glfwGetCursorPos(mPtr, x, y);
        return new float[]{(float) x[0], (float) y[0]};
    }
}
