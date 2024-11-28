package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private GameManager gameManager;

    public GamePanel(MainFrame frame) {
        this.gameManager = new GameManager();

        setLayout(new BorderLayout());
        // Background Panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw the background image
                ImageIcon background = new ImageIcon("polybridge/");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Top Toolbar
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(200, 200, 200));

        JButton placeJointButton = new JButton("Place Joint");
        JButton placeMemberButton = new JButton("Place Member");
        JButton selectToolButton = new JButton("Select Tool");
        JButton eraseToolButton = new JButton("Erase Tool");
        JButton undoButton = new JButton("Undo");
        JButton testBridgeButton = new JButton("Test Bridge");

        toolbar.add(placeJointButton);
        toolbar.add(placeMemberButton);
        toolbar.add(selectToolButton);
        toolbar.add(eraseToolButton);
        toolbar.add(undoButton);

        // Test Bridge Button in Top-Right Corner
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(testBridgeButton);

        add(toolbar, BorderLayout.NORTH);

        // Bridge Canvas (Interactive Area)
        BridgeCanvas canvas = new BridgeCanvas(gameManager);
        backgroundPanel.add(canvas, BorderLayout.CENTER);

        // Right Panel (Member Details)
        JPanel memberDetailsPanel = new JPanel();
        memberDetailsPanel.setLayout(new BoxLayout(memberDetailsPanel, BoxLayout.Y_AXIS));
        memberDetailsPanel.setPreferredSize(new Dimension(200, 0));
        memberDetailsPanel.setBorder(BorderFactory.createTitledBorder("Bridge Members"));

        JTextArea memberDetails = new JTextArea();
        memberDetails.setEditable(false);
        JScrollPane memberDetailsScroll = new JScrollPane(memberDetails);
        memberDetailsPanel.add(memberDetailsScroll);

        add(memberDetailsPanel, BorderLayout.EAST);

        // Button Actions
        placeJointButton.addActionListener(e -> canvas.setMode("placeJoint"));
        placeMemberButton.addActionListener(e -> canvas.setMode("placeMember"));
        selectToolButton.addActionListener(e -> canvas.setMode("select"));
        eraseToolButton.addActionListener(e -> canvas.setMode("erase"));
        undoButton.addActionListener(e -> canvas.undoLastAction());
        testBridgeButton.addActionListener(e -> {
            gameManager.simulate();
            String message = gameManager.isSimulationSuccess()
                    ? "The car successfully crossed the bridge!"
                    : "The bridge collapsed!";
            JOptionPane.showMessageDialog(frame, message);
        });
    }
}
