package org.cis1200.polybridge.truss;

import java.awt.*;
import java.util.Objects;

public class Vec2D {
    private double x;
    private double y;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public Vec2D add(Vec2D o) {
        return new Vec2D(getX() + o.getX(), getY() + o.getY());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2D vec2D = (Vec2D) o;
        return Double.compare(x, vec2D.x) == 0 && Double.compare(y, vec2D.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
