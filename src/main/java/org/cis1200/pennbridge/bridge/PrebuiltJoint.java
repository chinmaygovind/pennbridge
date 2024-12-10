package org.cis1200.pennbridge.bridge;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrebuiltJoint extends Joint {

    public static final List<PrebuiltJoint> BRIDGE_BUILTINS = new ArrayList<>(
            Arrays.asList(
                    new PrebuiltJoint(100, 400),
                    new PrebuiltJoint(200, 400),
                    new PrebuiltJoint(300, 400),
                    new PrebuiltJoint(400, 400),
                    new PrebuiltJoint(500, 400)
            )
    );

    public PrebuiltJoint(int x, int y) {
        super(x, y);
    }

    @Override
    public void move(int newX, int newY) {
        // cannot move
    }

    @Override
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
        gc.setColor(Color.BLACK);
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrebuiltJoint joint = (PrebuiltJoint) o;
        return getX() == joint.getX() && getY() == joint.getY();
    }

    @Override
    public String toString() {
        return String.format("Prebuilt Joint: (%d, %d)", getX(), getY());
    }

}
