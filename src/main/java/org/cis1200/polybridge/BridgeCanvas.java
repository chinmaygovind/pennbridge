package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BridgeCanvas extends JPanel {
    private String currentMode = "select";
    private ArrayList<String> actions = new ArrayList<>();

    private GameManager gameManager;

    public BridgeCanvas(GameManager gameManager) {
        this.gameManager = gameManager;

        setBackground(new Color(255, 255, 255));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    public void setMode(String mode) {
        currentMode = mode;
    }

    public void undoLastAction() {
        if (!actions.isEmpty()) {
            actions.remove(actions.size() - 1);
            repaint();
        }
    }

    private void handleMouseClick(MouseEvent e) {
        switch (currentMode) {
            case "placeJoint":
                // Add a joint at the clicked location
                gameManager.getBridge().addNode(e.getX(), e.getY());
                actions.add("Joint added");
                break;
            case "placeMember":
                // Logic for placing a member
                actions.add("Member added");
                break;
            case "erase":
                // Logic for erasing an element
                actions.add("Element erased");
                break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw bridge components
        for (Joint joint : gameManager.getBridge().getJoints()) {
            g.setColor(Color.BLUE);
            g.fillOval(joint.getX() - 5, joint.getY() - 5, 10, 10);
        }
        for (Member member : gameManager.getBridge().getMembers()) {
            g.setColor(Color.BLACK);
            g.drawLine(member.getStart().getX(), member.getStart().getY(),
                    member.getEnd().getX(), member.getEnd().getY());
        }
    }
}
