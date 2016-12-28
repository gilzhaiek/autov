package com.eightman.autov.Objects.Geom;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public final class XY {
    private final double x;
    private final double y;

    public XY(XY xy) {
        this.x = xy.getX();
        this.y = xy.getY();
    }

    public XY(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public XY add(XY xy) {
        return new XY(x + xy.getX(), y + xy.getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVector() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XY)) {
            return false;
        }

        return x == ((XY) obj).getX() && y == ((XY) obj).getY();
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}