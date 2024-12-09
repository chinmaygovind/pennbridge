package org.cis1200.polybridge;

import org.cis1200.polybridge.components.Bridge;
import org.cis1200.polybridge.components.BridgeComponent;
import org.cis1200.polybridge.components.Traveler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.cis1200.polybridge.components.Traveler.*;

public class BridgeSimulator extends JFrame {

    private Bridge bridge;
    private static final int SIMULATOR_WIDTH = 600;
    private static final int SIMULATOR_HEIGHT = 565; // idk

    public BridgeSimulator(Bridge bridge) {
        this.bridge = bridge;
        setTitle("Simulate Bridge");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(SIMULATOR_WIDTH, SIMULATOR_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new BridgeSimulatorPanel());
    }

    class BridgeSimulatorPanel extends JPanel {

        private final BufferedImage backgroundImg;
        private Traveler traveler;

        private static final int FRAME_LENGTH = 16; // 16 ms = 60fps

        public BridgeSimulatorPanel() {

            setBackground(Color.WHITE);
            try {
                backgroundImg = ImageIO.read(new File(GamePanel.BACKGROUND_IMAGE));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read BridgeCanvas BACKGROUND_IMAGE: " + e);
            }

            this.traveler = new Traveler(new Point(TRAVELER_START_X, DECK_HEIGHT));
            Timer timer = new Timer(FRAME_LENGTH, e -> update());
            timer.start();
        }

        private static final Point TRAVELER_INCREMENT = new Point(1, 0);

        public void update() {
            traveler.translate(TRAVELER_INCREMENT);
            System.out.println("traveler moving right");
            repaint();
        }

        @Override
        public void paintComponent(Graphics gc) {

            // Draw Background
            if (backgroundImg != null) {
                gc.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            }
            traveler.draw(gc);
            for (BridgeComponent bc : bridge.getCompiledBridgeComponents()) {
                bc.draw(gc);
            }

        }
    }

}
