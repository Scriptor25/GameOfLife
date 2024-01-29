package io.scriptor.gol.game;

public class GOLFieldT<T> {

    public T x;
    public T y;
    public boolean state;

    public GOLFieldT(T x, T y, boolean state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }
}
