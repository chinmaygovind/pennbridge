package org.cis1200.polybridge;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class Joint extends BridgeComponent {
    private LinkedList<Point> pointHistory;
    public static final int JOINT_RADIUS = 5;

    public Joint(int x, int y) {
        int snappedX = (int) Math.round(x / 20.0) * 20;
        int snappedY = (int) Math.round(y / 20.0) * 20;
        pointHistory = new LinkedList<>(Collections.singletonList(new Point(snappedX, snappedY)));
    }

    public int getX() {
        return (int) getPoint().getX();
    }
    public int getY() {
        return (int) getPoint().getY();
    }

    public Point getPoint() {
        return pointHistory.getLast();
    }

    public static Point getSnappedPoint(int x, int y) {
        int snappedX = (int) Math.round(x / 20.0) * 20;
        int snappedY = (int) Math.round(y / 20.0) * 20;
        return new Point(snappedX, snappedY);
    }

    public void move(int newX, int newY) {
        pointHistory.add(new Point(newX, newY));
    }

    public void draw(Graphics gc0) {
        Graphics2D gc = (Graphics2D) gc0;
        if (highlighted) {
            gc.setColor(Color.GRAY);
            gc.setStroke(new BasicStroke(3));
            gc.fillOval(getX() - JOINT_RADIUS, getY() - JOINT_RADIUS,
                    2 * JOINT_RADIUS, 2 * JOINT_RADIUS);

        }
        gc.setColor(Color.BLUE);
        gc.setStroke(new BasicStroke(3));
        gc.drawOval(getX() - JOINT_RADIUS, getY() - JOINT_RADIUS,
                2 * JOINT_RADIUS, 2 * JOINT_RADIUS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joint joint = (Joint) o;
        return getX() == joint.getX() && getY() == joint.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return String.format("Joint: (%d, %d)", getX(), getY());
    }
}

