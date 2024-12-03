package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;

import static org.cis1200.polybridge.Joint.JOINT_RADIUS;

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
        int snappedX = (int) Math.round(this.x / 20.0) * 20;
        int snappedY = (int) Math.round(this.y / 20.0) * 20;
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

