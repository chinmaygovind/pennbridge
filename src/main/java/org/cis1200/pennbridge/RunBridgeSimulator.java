package org.cis1200.pennbridge;

// imports necessary libraries for Java swing

import org.cis1200.pennbridge.bridge.Bridge;

import javax.swing.*;

/**
 * Another Main class that specifies the frame and widgets of the GUI for the
 * bridge simulator
 */
public class RunBridgeSimulator implements Runnable {
    private final Bridge bridge;

    public RunBridgeSimulator(Bridge bridge) {
        this.bridge = bridge;
    }

    public void run() {
        JFrame frame = new BridgeSimulator(bridge);
        frame.setVisible(true);
    }

}