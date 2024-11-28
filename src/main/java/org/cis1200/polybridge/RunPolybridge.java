package org.cis1200.polybridge;

// imports necessary libraries for Java swing

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunPolybridge implements Runnable {
    public void run() {
        JFrame frame = new MainFrame();
        frame.setVisible(true);
    }

}