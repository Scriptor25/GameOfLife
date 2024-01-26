package io.scriptor.gol.game;

import io.scriptor.gol.scene.GOLMesh;
import io.scriptor.gol.scene.GOLVertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GOLGame {

    private final Map<GOLPosition, GOLField> mFields = new HashMap<>();
    private final GOLMesh mMesh = new GOLMesh();
    private float mTime = 0;

    public int count() {
        return mMesh.count();
    }

    public void bind() {
        mMesh.bind();
    }

    public void unbind() {
        mMesh.unbind();
    }

    public GOLGame set(int x, int y, boolean alive) {
        final var field = mFields.computeIfAbsent(new GOLPosition(x, y), pos -> new GOLField());
        if (alive) field.lives();
        else field.dies();
        field.refresh();
        return this;
    }

    public GOLGame toggle(int x, int y) {
        final var field = mFields.computeIfAbsent(new GOLPosition(x, y), pos -> new GOLField());
        if (field.alive()) field.dies();
        else field.lives();
        field.refresh();
        return this;
    }

    public void step(float delta) {

        if (mTime < 0.1f) {
            mTime += delta;
            return;
        }
        mTime = 0;

        final Vector<GOLFieldAt> fields = new Vector<>();
        for (final var entry : mFields.entrySet())
            fields.add(new GOLFieldAt(entry.getKey(), entry.getValue()));

        final Map<GOLPosition, GOLField> next = new HashMap<>();
        while (!fields.isEmpty()) {
            final var fieldAt = fields.removeFirst();
            final var pos = fieldAt.pos();
            final var field = new GOLField(fieldAt.field());

            if (field.timeout() || next.containsKey(pos))
                continue;

            // count neighbors:
            // 1. Any live cell with fewer than two live neighbors dies, as if by underpopulation.
            // 2. Any live cell with two or three live neighbors lives on to the next generation.
            // 3. Any live cell with more than three live neighbors dies, as if by overpopulation.
            // 4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

            int neighbors = 0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    if (i == 0 && j == 0)
                        continue;
                    final var np = new GOLPosition(pos.x() + i, pos.y() + j);
                    var nf = mFields.get(np);
                    if (nf == null) {
                        nf = new GOLField();
                        if (field.alive()) // only generate new cells around alive cells, they do NOT matter to dead ones!
                            fields.add(new GOLFieldAt(np, nf));
                    }

                    if (nf.alive())
                        neighbors++;
                }

            if (!field.alive()) {
                if (neighbors == 3) {
                    field.refresh();
                    field.lives();
                } else {
                    field.tick();
                }
                next.put(pos, field);
            } else {
                if (neighbors < 2 || neighbors > 3) field.dies();
                else field.refresh();
            }

            next.put(pos, field);
        }

        mFields.clear();
        mFields.putAll(next);
    }

    public void generateMesh() {
        mMesh.clear();
        int idx = 0;
        for (final var entry : mFields.entrySet()) {
            final var pos = entry.getKey();
            final var field = entry.getValue();
            final var gb = field.alive() ? 1.0f : 0.0f;
            final var a = field.percent();
            mMesh
                    .addVertex(new GOLVertex(pos.x() - 0.5f, pos.y() - 0.5f, 1.0f, gb, gb, a))
                    .addVertex(new GOLVertex(pos.x() - 0.5f, pos.y() + 0.5f, 1.0f, gb, gb, a))
                    .addVertex(new GOLVertex(pos.x() + 0.5f, pos.y() + 0.5f, 1.0f, gb, gb, a))
                    .addVertex(new GOLVertex(pos.x() + 0.5f, pos.y() - 0.5f, 1.0f, gb, gb, a))
                    .addFace(idx + 0, idx + 1, idx + 2)
                    .addFace(idx + 2, idx + 3, idx + 0);
            idx += 4;
        }
        mMesh.apply();
    }
}
