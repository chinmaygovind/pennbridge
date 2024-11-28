package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    public MenuPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Title Label
        JLabel title = new JLabel("Polybridge", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        add(title, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton helpButton = new JButton("HELP");
        JButton playButton = new JButton("PLAY");

        buttonPanel.add(helpButton);
        buttonPanel.add(playButton);

        // Add spacing and center buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Button Actions
        helpButton.addActionListener(e -> frame.setScreen(new HelpPanel(frame)));
        playButton.addActionListener(e -> frame.setScreen(new GamePanel(frame)));
    }
}
