package org.cis1200.polybridge.components;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class Joint extends BridgeComponent {
    private LinkedList<Point> pointHistory;
    public static final int JOINT_RADIUS = 4;

    public Joint(int x, int y) {
        Point snapped = getSnappedPoint(x, y);
        int snappedX = (int) snapped.getX();
        int snappedY = (int) snapped.getY();
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
        int snappedX = (int) Math.round(x / 25.0) * 25;
        int snappedY = (int) Math.round(y / 25.0) * 25;
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
            gc.fillOval(
                    getX() - JOINT_RADIUS, getY() - JOINT_RADIUS,
                    2 * JOINT_RADIUS, 2 * JOINT_RADIUS
            );

        }
        gc.setColor(Color.BLUE);
        gc.setStroke(new BasicStroke(3));
        gc.drawOval(
                getX() - JOINT_RADIUS, getY() - JOINT_RADIUS,
                2 * JOINT_RADIUS, 2 * JOINT_RADIUS
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Joint)) {
            return false;
        }
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
