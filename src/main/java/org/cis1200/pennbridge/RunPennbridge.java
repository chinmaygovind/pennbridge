package org.cis1200.pennbridge;

// imports necessary libraries for Java swing

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunPennbridge implements Runnable {
    public void run() {
        JFrame frame = new MainFrame();
        frame.setVisible(true);
    }

}