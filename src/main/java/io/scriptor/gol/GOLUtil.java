package io.scriptor.gol;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GOLUtil {

    private GOLUtil() {
    }

    public static void initializeGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new RuntimeException();
    }

    public static void terminateGLFW() {
        glfwTerminate();
        final var callback = glfwSetErrorCallback(null);
        if (callback != null)
            callback.close();
    }

    public static void initOpenGL() {
        GL.createCapabilities();
        glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
    }

    public static void frameStart(int width, int height) {
        glViewport(0, 0, width, height);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void frameEnd() {
        glDisable(GL_BLEND);
    }
}
