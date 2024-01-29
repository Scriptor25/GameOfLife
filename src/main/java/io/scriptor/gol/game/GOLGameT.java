package io.scriptor.gol.game;

import io.scriptor.gol.scene.GOLMesh;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GOLGameT<T extends Number> {

    public static <T extends Number> boolean get(Map<T, Map<T, Boolean>> cells, T x, T y) {
        return cells.containsKey(y) && cells.get(y).getOrDefault(x, false);
    }

    public static <T extends Number> void put(Map<T, Map<T, Boolean>> cells, T x, T y, boolean state) {
        cells.computeIfAbsent(y, key -> new HashMap<>()).put(x, state);
    }

    public static <T extends Number> T cast(int i, Class<T> clazz) {
        if (clazz == Byte.class) return clazz.cast((byte) i);
        if (clazz == Short.class) return clazz.cast((short) i);
        if (clazz == Integer.class) return clazz.cast(i);
        if (clazz == Long.class) return clazz.cast((long) i);
        throw new RuntimeException();
    }

    private final Map<T, Map<T, Boolean>> mCells = new HashMap<>();
    private final GOLMesh mMesh = new GOLMesh();
    private final Class<T> mClass;
    private float mTime = 0;

    public GOLGameT(Class<T> clazz) {
        mClass = clazz;
    }

    public int count() {
        return mMesh.count();
    }

    public void bind() {
        mMesh.bind();
    }

    public void unbind() {
        mMesh.unbind();
    }

    public GOLGameT<T> set(T x, T y, boolean alive) {
        mCells.computeIfAbsent(y, key -> new HashMap<>()).put(x, alive);
        return this;
    }

    public GOLGameT<T> toggle(T x, T y) {
        return set(x, y, !get(mCells, x, y));
    }

    public void step(float delta) {
        if (mTime < 0.01f) {
            mTime += delta;
            return;
        }
        mTime = 0;

        final Vector<GOLFieldT<T>> fields = new Vector<>();
        for (final var yEntries : mCells.entrySet()) {
            final var y = yEntries.getKey();
            final var row = yEntries.getValue();
            for (final var xEntries : row.entrySet()) {
                final var x = xEntries.getKey();
                final var state = xEntries.getValue();
                fields.add(new GOLFieldT<>(x, y, state));
            }
        }

        final Map<T, Map<T, Boolean>> nextCells = new HashMap<>();
        while (!fields.isEmpty()) {
            final var field = fields.removeFirst();
            final var x = field.x;
            final var y = field.y;
            var state = field.state;

            int nc = 0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    if (i == 0 && j == 0)
                        continue;

                    final var dx = cast(x.intValue() + i, mClass);
                    final var dy = cast(y.intValue() + j, mClass);
                    if (get(mCells, dx, dy)) nc++;
                    else if (state) fields.add(new GOLFieldT<>(dx, dy, false));
                }

            if (!state && nc == 3)
                state = true;
            else if (state && (nc < 2 || nc > 3))
                state = false;

            if (state)
                put(nextCells, x, y, state);
        }

        mCells.clear();
        mCells.putAll(nextCells);
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
                        .addFace(idx + 0, idx + 1, idx + 2)
                        .addFace(idx + 2, idx + 3, idx + 0);
                idx += 4;
            }
        }
        mMesh.apply();
    }
}
