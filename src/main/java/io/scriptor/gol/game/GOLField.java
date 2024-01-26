package io.scriptor.gol.game;

public class GOLField {

    private static final int MAX_TICK = 10;

    private int mTick = MAX_TICK;
    private boolean mAlive = false;

    public GOLField() {
    }

    public GOLField(GOLField field) {
        this.mAlive = field.mAlive;
        this.mTick = field.mTick;
    }

    public boolean timeout() {
        return mTick <= 0;
    }

    public boolean alive() {
        return mAlive;
    }

    public void tick() {
        mTick--;
    }

    public void refresh() {
        mTick = MAX_TICK;
    }

    public void dies() {
        mAlive = false;
    }

    public void lives() {
        mAlive = true;
    }

    public float percent() {
        return (float) mTick / MAX_TICK;
    }
}
