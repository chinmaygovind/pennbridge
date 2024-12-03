package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;

public class HelpPanel extends JPanel {
    public HelpPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("How to Play", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JTextArea instructions = new JTextArea();
        instructions.setText("""
            Welcome to Bridge Builder Game!
            
            1. Use your mouse to place bridge nodes and connect them with segments.
            2. Press the "Simulate" button to test if your bridge can support the car.
            3. If the car successfully crosses, you win!
            4. If the bridge collapses, adjust your design and try again.

            Tips:
            - Use triangles for better structural stability.
            - Watch out for uneven weight distribution!
            """);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(instructions);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> frame.setScreen(new MenuPanel(frame)));
        add(backButton, BorderLayout.SOUTH);
    }
}
