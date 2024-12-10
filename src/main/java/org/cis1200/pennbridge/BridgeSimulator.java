package org.cis1200.pennbridge;

import org.cis1200.pennbridge.components.Bridge;
import org.cis1200.pennbridge.components.BridgeComponent;
import org.cis1200.pennbridge.components.Traveler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.cis1200.pennbridge.components.Traveler.*;

public class BridgeSimulator extends JFrame {

    private Bridge bridge;
    private static final int SIMULATOR_WIDTH = 600;
    private static final int SIMULATOR_HEIGHT = 565; // idk

    public BridgeSimulator(Bridge bridge) {
        this.bridge = Bridge.loadFromString(bridge.toString()); // shhhh
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
        private boolean bridgeFailed = false;
        private Timer updateTimer;

        public BridgeSimulatorPanel() {

            setBackground(Color.WHITE);
            try {
                backgroundImg = ImageIO.read(new File(GamePanel.BACKGROUND_IMAGE));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read BridgeCanvas BACKGROUND_IMAGE: " + e);
            }

            this.traveler = new Traveler(new Point(TRAVELER_START_X, DECK_HEIGHT));
            this.updateTimer = new Timer(FRAME_LENGTH, e -> update());
            this.updateTimer.start();
        }

        private static final Point TRAVELER_INCREMENT = new Point(1, 0);
        private static final Point TRAVELER_RESET = new Point(-800, 0);

        public void update() {
            traveler.translate(TRAVELER_INCREMENT);
            int travelerX = traveler.getX();
            boolean bridgeIntact = true;
            if (travelerX >= Bridge.BRIDGE_LEFT_X && travelerX < Bridge.BRIDGE_RIGHT_X) {
                bridgeIntact = bridgeIntact && bridge.solveMemberForces(travelerX, WEIGHT);
            }
            if (travelerX > SIMULATOR_WIDTH) {
                traveler.translate(TRAVELER_RESET);
            }
            if (!bridgeIntact) {
                bridgeFailed = true;
                traveler.setTravelerImage(TRAVELER_IMAGE_SAD);
                updateTimer.stop();
            }
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
