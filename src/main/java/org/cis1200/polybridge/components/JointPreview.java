package org.cis1200.polybridge.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

import static org.cis1200.polybridge.components.Joint.JOINT_RADIUS;

public class JointPreview extends BridgeComponent {

    private JPanel canvas;
    private int x, y;

    public JointPreview(int x, int y, JPanel canvas) {
        this.x = x;
        this.y = y;
        this.canvas = canvas;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void draw(Graphics gc0) {
        // snap to grid
        Point2D snapped = Joint.getSnappedPoint(this.x, this.y);
        int snappedX = (int) snapped.getX();
        int snappedY = (int) snapped.getY();
        Graphics2D gc = (Graphics2D) gc0;
        //draw horizontal and vertical line
        gc.setColor(Color.BLACK);
        gc.setStroke(new BasicStroke(1));
        gc.drawLine(0, snappedY, (int) canvas.getSize().getWidth(), snappedY);
        gc.drawLine(snappedX, 0, snappedX, (int) canvas.getSize().getHeight());
        gc.setStroke(new BasicStroke(3));
        gc.drawOval(snappedX - JOINT_RADIUS, snappedY - JOINT_RADIUS,
                2 * JOINT_RADIUS, 2 * JOINT_RADIUS);
    }
}

