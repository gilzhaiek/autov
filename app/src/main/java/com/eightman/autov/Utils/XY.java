package com.eightman.autov.Utils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public final class XY {
    private final double x;
    private final double y;

    public XY(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XY)) {
            return false;
        }

        return x == ((XY)obj).getX() && y == ((XY)obj).getY();
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}