package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("PolyBridge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setScreen(new MenuPanel(this));
    }

    public void setScreen(JPanel screen) {
        getContentPane().removeAll();
        add(screen);
        revalidate();
        repaint();
    }
}