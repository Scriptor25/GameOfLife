package io.scriptor.gol.game;

public record GOLPosition(int x, int y) {
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof GOLPosition other)) return false;
        return this.x == other.x && this.y == other.y;
    }
}
