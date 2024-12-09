package org.cis1200.polybridge.components;

import java.awt.*;

public class MemberPreview extends BridgeComponent {
    private Joint start;
    private int endX;
    private int endY;

    public MemberPreview(Joint start, int endX, int endY) {
        this.start = start;
        this.endX = endX;
        this.endY = endY;
    }

    public Joint getStart() {
        return start;
    }

    public void draw(Graphics gc0) {
        Graphics2D gc = (Graphics2D) gc0;
        gc.setColor(Color.GRAY);
        gc.setStroke(
                new BasicStroke(
                        1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 10.0f }, 0.0f
                )
        );
        gc.drawLine(getStart().getX(), getStart().getY(), endX, endY);

    }
}
