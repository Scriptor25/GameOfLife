package io.scriptor.gol.game;

import io.scriptor.gol.scene.GOLMesh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GOLGame {

    public static boolean get(Map<Integer, Map<Integer, Boolean>> cells, int x, int y) {
        return cells.containsKey(y) && cells.get(y).getOrDefault(x, false);
    }

    public static void put(Map<Integer, Map<Integer, Boolean>> cells, int x, int y, boolean state) {
        cells.computeIfAbsent(y, key -> new HashMap<>()).put(x, state);
    }

    private final Map<Integer, Map<Integer, Boolean>> mCells = new HashMap<>();
    private final GOLMesh mMesh = new GOLMesh();
    private float mTime = 0;

    public void draw() {
        mMesh.draw();
    }

    public void set(int x, int y, boolean alive) {
        mCells.computeIfAbsent(y, key -> new HashMap<>()).put(x, alive);
    }

    public void toggle(int x, int y) {
        set(x, y, !get(mCells, x, y));
    }

    public int step(float delta) {
        if (mTime < 0.01f) {
            mTime += delta;
            return 0;
        }
        mTime = 0;

        final List<GOLField> fields = new Vector<>();
        for (final var yEntries : mCells.entrySet()) {
            final var y = yEntries.getKey();
            final var row = yEntries.getValue();
            for (final var xEntries : row.entrySet()) {
                final var x = xEntries.getKey();
                final var state = xEntries.getValue();
                fields.add(new GOLField(x, y, state));
            }
        }

        final Map<Integer, Map<Integer, Boolean>> nextCells = new HashMap<>();
        int change = 0;

        while (!fields.isEmpty()) {
            final var field = fields.removeFirst();
            final var x = field.x;
            final var y = field.y;
            final var prevState = field.state;

            int nc = 0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    if (i == 0 && j == 0)
                        continue;

                    final var dx = x + i;
                    final var dy = y + j;
                    if (GOLGame.get(mCells, dx, dy)) nc++;
                    else if (prevState) fields.add(new GOLField(dx, dy, false));
                }

            final boolean state = (!prevState && nc == 3) || (prevState && (nc == 2 || nc == 3));
            if (state) GOLGame.put(nextCells, x, y, true);

            if (state != prevState)
                change++;
        }

        mCells.clear();
        mCells.putAll(nextCells);

        return change > 0 ? 1 : 2;
    }

    public void generateMesh() {
        mMesh.clear();
        int idx = 0;
        for (final var yEntries : mCells.entrySet()) {
            final var y = yEntries.getKey().floatValue();
            final var row = yEntries.getValue();
            for (final var xEntries : row.entrySet()) {
                final var x = xEntries.getKey().floatValue();
                final var cell = xEntries.getValue();
                final var a = cell ? 1.0f : 0.0f;
                mMesh
                        .addVertex(x - 0.5f, y - 0.5f, a)
                        .addVertex(x - 0.5f, y + 0.5f, a)
                        .addVertex(x + 0.5f, y + 0.5f, a)
                        .addVertex(x + 0.5f, y - 0.5f, a)
                        .addFace(idx, idx + 1, idx + 2)
                        .addFace(idx + 2, idx + 3, idx);
                idx += 4;
            }
        }
        mMesh.apply();
    }
}
