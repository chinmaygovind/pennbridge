package org.cis1200.polybridge;

import org.cis1200.polybridge.components.Shape;

import java.awt.*;

public class SelectPreview extends Shape {
    private int x, y, width, height;

    public SelectPreview(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public void draw(Graphics gc0) {
        Graphics2D gc = (Graphics2D) gc0;
        gc.setColor(Color.GRAY);
        gc.setStroke(
                new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                        10.0f, new float[]{10.0f}, 0.0f));
        gc.drawRect(x, y, width, height);

    }
}
