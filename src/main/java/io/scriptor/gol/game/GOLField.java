package io.scriptor.gol.game;

public class GOLField {

    public int x;
    public int y;
    public boolean state;

    public GOLField(int x, int y, boolean state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }
}
