package io.scriptor.gol.scene;

import io.scriptor.gol.graphics.GOLBuffer;
import io.scriptor.gol.graphics.GOLVertexArray;
import org.lwjgl.system.MemoryUtil;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GOLMesh {

    private final List<GOLVertex> mVertices = new Vector<>();
    private final List<Integer> mIndices = new Vector<>();

    private final GOLVertexArray mVertexArray = new GOLVertexArray();
    private final GOLBuffer mVertexBuffer = new GOLBuffer(GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
    private final GOLBuffer mIndexBuffer = new GOLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_DYNAMIC_DRAW);

    @Override
    public String toString() {
        return String.format("Vertices: %s%nIndices: %s", mVertices, mIndices);
    }

    public int count() {
        return mIndices.size();
    }

    public GOLMesh bind() {
        mVertexArray.bind();
        mIndexBuffer.bind();
        return this;
    }

    public void unbind() {
        mVertexArray.unbind();
        mIndexBuffer.unbind();
    }

    public void draw() {
        bind();
        glDrawElements(GL_TRIANGLES, count(), GL_UNSIGNED_INT, NULL);
        unbind();
    }

    public GOLMesh apply() {
        {
            final var data = MemoryUtil.memAlloc(GOLVertex.BYTES * mVertices.size());
            for (final var vertex : mVertices)
                vertex.copyTo(data);
            data.position(0);

            mVertexArray.bind();
            mVertexBuffer.bind();
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, GOLVertex.BYTES, 0);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, GOLVertex.BYTES, 3 * Float.BYTES);
            mVertexBuffer.setData(data).unbind();
            mVertexArray.unbind();

            MemoryUtil.memFree(data);
        }

        {
            final var data = MemoryUtil.memAllocInt(mIndices.size());
            for (final var index : mIndices)
                data.put(index);
            data.position(0);
            mIndexBuffer.bind().setData(data).unbind();

            MemoryUtil.memFree(data);
        }

        return this;
    }

    public GOLMesh clear() {
        mVertices.clear();
        mIndices.clear();
        return this;
    }

    public GOLMesh addVertex(GOLVertex vertex) {
        mVertices.add(vertex);
        return this;
    }

    public GOLMesh addVertex(float x, float y, float a) {
        mVertices.add(new GOLVertex(x, y, a));
        return this;
    }

    public GOLMesh addFace(int i0, int i1, int i2) {
        mIndices.add(i0);
        mIndices.add(i1);
        mIndices.add(i2);
        return this;
    }
}
