package io.scriptor.gol;

import io.scriptor.gol.game.GOLGame;
import io.scriptor.gol.graphics.GOLProgram;
import io.scriptor.gol.graphics.GOLWindow;
import io.scriptor.gol.scene.GOLMesh;
import io.scriptor.gol.scene.GOLVertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private static float zoom = 16f;

    public static void main(String[] args) throws IOException {

        GOLWindow.init();

        final var window = new GOLWindow(800, 600, "Game Of Life");

        GL.createCapabilities();
        glClearColor(0.05f, 0.05f, 0.05f, 1.0f);

        final var selector = new GOLMesh()
                .clear()
                .addVertex(new GOLVertex(-0.5f, -0.5f, 0.2f))
                .addVertex(new GOLVertex(-0.5f, 0.5f, 0.2f))
                .addVertex(new GOLVertex(0.5f, 0.5f, 0.2f))
                .addVertex(new GOLVertex(0.5f, -0.5f, 0.2f))
                .addFace(0, 1, 2)
                .addFace(2, 3, 0)
                .apply();

        //final var game = new GOLGameT<>(Integer.class);
        final var game = new GOLGame();
        /*game
                .set(0, 0, true).set(1, 0, true).set(2, 0, true).set(3, 0, true).set(4, 0, true)
                .set(-1, -1, true).set(4, -1, true)
                .set(4, -2, true)
                .set(-1, -3, true).set(3, -3, true)
                .set(1, -4, true);*/ // (LWSS)

        final var program = new GOLProgram("shaders/main.vsh", "shaders/main.fsh");

        window.register((ptr, xoffset, yoffset) -> {
            zoom -= (float) yoffset;
            if (zoom < 1f)
                zoom = 1f;
        });

        final var translation = new Vector3f();

        boolean prevLeft = false;
        boolean prevSpace = false;

        boolean paused = false;

        long frame = System.currentTimeMillis();
        while (window.spin()) {
            long now = System.currentTimeMillis();
            float delta = (now - frame) / 1000.0f;
            frame = now;

            final var size = window.getSize();
            final var pos = window.getMousePos();

            if (!paused) game.step(delta);
            game.generateMesh();

            final var a = (float) size[0] / size[1];
            final var proj = new Matrix4f().setOrtho2D(-a * zoom, a * zoom, -zoom, zoom);
            final var view = new Matrix4f().setTranslation(translation).invert();
            final var model = new Matrix4f();
            //final var proj = new Matrix4f().setPerspective(Math.toRadians(90.0f), a, 0.01f, 100.0f);
            //final var view = new Matrix4f().setLookAt(0.0f, 0.0f, -2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

            var x = pos[0] / size[0]; // 0 -> 1
            var y = pos[1] / size[1]; // 0 -> 1
            x = 2 * a * x - a; // -a -> a
            y = 2 * y - 1; // -1 -> 1
            y = -y; // invert y to be -1 -> 1 from bottom to top

            final var px = Math.round(x * zoom + translation.x);
            final var py = Math.round(y * zoom + translation.y);

            final var selectorModel = new Matrix4f().setTranslation(px, py, 0.0f);

            final var speed = delta * 20;
            if (window.getKey(GLFW_KEY_W)) translation.y += speed;
            if (window.getKey(GLFW_KEY_S)) translation.y -= speed;
            if (window.getKey(GLFW_KEY_D)) translation.x += speed;
            if (window.getKey(GLFW_KEY_A)) translation.x -= speed;

            final var space = window.getKey(GLFW_KEY_SPACE);
            if (space && !prevSpace)
                paused = !paused;
            prevSpace = space;

            final var left = window.getMouseButton(GLFW_MOUSE_BUTTON_LEFT);
            if (left && !prevLeft)
                game.toggle(px, py);
            prevLeft = left;

            glViewport(0, 0, size[0], size[1]);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            program
                    .bind()
                    .uniform("uProjection", loc -> glUniformMatrix4fv(loc, false, proj.get(new float[16])))
                    .uniform("uView", loc -> glUniformMatrix4fv(loc, false, view.get(new float[16])))
                    .uniform("uModel", loc -> glUniformMatrix4fv(loc, false, model.get(new float[16])));
            game.bind();
            glDrawElements(GL_TRIANGLES, game.count(), GL_UNSIGNED_INT, NULL);
            game.unbind();

            program.uniform("uModel", loc -> glUniformMatrix4fv(loc, false, selectorModel.get(new float[16])));
            selector.bind();
            glDrawElements(GL_TRIANGLES, selector.count(), GL_UNSIGNED_INT, NULL);
            selector.unbind();

            program.unbind();
            glDisable(GL_BLEND);
        }

        GOLWindow.terminate();
    }
}
