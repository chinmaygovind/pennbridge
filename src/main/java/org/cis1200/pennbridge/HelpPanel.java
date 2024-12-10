package org.cis1200.pennbridge;

import javax.swing.*;
import java.awt.*;

public class HelpPanel extends JPanel {
    public HelpPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("How to Play", SwingConstants.CENTER);
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JTextArea instructions = new JTextArea();
        instructions.setText(
            """
            Welcome to Pennbridge!
            You are an engineer tasked with designing a new bridge 
            to get from Locust to 1920 Commons. Use this tool to design 
            the bridge and get funding from the Penn Board of Trustees!
    
            1. Use the "Joint" tool to place joints. 
            There are 5 pre-built joints along the deck bridge.
            2. Use the "Member" tool to click and drag beams between the joints. 
            Remember to put members along the bridge deck.
            3. If you make a mistake, use the "Select" tool to move 
            joints and members around, or use the "Undo" tool to undo.
            4. When you're ready, hit "Test Bridge" to see if your bridge works!
    
            Tips:
            - If you're looking for inspiration, try Loading in one of the sample bridges.
            - To share your bridge, Save your bridge to a .bridge file to send to your friends!
            - Use triangles for better structural stability.
            - Watch out for uneven weight distribution!
            """
        );
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
