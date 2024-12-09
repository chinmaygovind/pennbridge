package org.cis1200.polybridge.components;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Traveler extends Shape {
    private static final String TRAVELER_IMAGE = "files/polybridge/traveler.png";
    private static BufferedImage travelerImg;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 60;
    public static final int TRAVELER_START_X = 50;
    public static final int DECK_HEIGHT = 385;

    public static final int WEIGHT = 500;

    private Point position;

    public Traveler(Point position) {
        this.position = position;
        try {
            travelerImg = ImageIO.read(new File(TRAVELER_IMAGE));
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Failed to read traveler image at %s", TRAVELER_IMAGE)
            );
        }
    }

    public void translate(Point movement) {
        position = new Point(
                (int) (position.getX() + movement.getX()),
                (int) (position.getY() + movement.getY())
        );
    }

    @Override
    public void draw(Graphics gc) {
        gc.drawImage(
                travelerImg,
                (int) (position.getX() - WIDTH / 2),
                (int) position.getY() - HEIGHT,
                WIDTH,
                HEIGHT,
                null
        );
    }
}