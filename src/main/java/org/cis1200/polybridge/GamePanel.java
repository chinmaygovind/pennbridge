package org.cis1200.polybridge;

import org.cis1200.polybridge.components.*;
import org.cis1200.polybridge.components.Shape;
import org.cis1200.polybridge.truss.Bridge;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    private final Bridge bridge;
    private final BridgeCanvas bridgeCanvas;

    private static final int HIGHLIGHT_TOLERANCE = 20;

    interface Mode extends MouseListener, MouseMotionListener { }
    class JointMode extends MouseAdapter implements Mode {
        Mode previousMode;
        public JointMode() {
            this.previousMode = this;
        }
        public JointMode(Mode previousMode) {
            this.previousMode = previousMode;
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            System.out.println("Previewing joint at " + e.getX() + ", " + e.getY());
            preview = new JointPreview(e.getX(), e.getY(), bridgeCanvas);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // System.out.println("Adding joint at " + e.getX() + ", " + e.getY());
            bridge.addJoint(e.getX(), e.getY());
            bridgeCanvas.repaint();
            setMode(previousMode);
        }
    }

    class MemberStartMode extends MouseAdapter implements Mode {
        @Override
        public void mouseMoved(MouseEvent e) {
            Joint j = bridge.getClosestJoint(e.getX(), e.getY(), HIGHLIGHT_TOLERANCE);
            if (j != null) {
                bridgeCanvas.highlighted.add(j);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Joint j = bridge.getClosestJoint(e.getX(), e.getY(), 20);
            if (j == null) {
                return;
            }
            setMode(new MemberEndMode(j));
        }
    }

    class MemberEndMode extends MouseAdapter implements Mode {

        private final Joint startJoint;
        public MemberEndMode(Joint startJoint) {
            this.startJoint = startJoint;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            preview = new MemberPreview(startJoint, e.getX(), e.getY());
            Joint j = bridge.getClosestJoint(e.getX(), e.getY(), HIGHLIGHT_TOLERANCE);
            bridgeCanvas.highlighted.add(j);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Joint endJoint = bridge.getClosestJoint(e.getX(), e.getY(), HIGHLIGHT_TOLERANCE);
            if (endJoint != null && endJoint != startJoint) {
                bridge.addMember(startJoint, endJoint);
            }
            setMode(new MemberStartMode());
        }
    }
    class SelectStartMode extends MouseAdapter implements Mode {
        @Override
        public void mouseMoved(MouseEvent e) {
            Joint closest = bridge.getClosestJoint(e.getX(), e.getY(), HIGHLIGHT_TOLERANCE);
            if (closest != null) {
                bridgeCanvas.highlighted.add(closest);
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
            bridgeCanvas.selected.clear();
            Joint closest = bridge.getClosestJoint(e.getX(), e.getY(), HIGHLIGHT_TOLERANCE);
            if (closest == null) {
                setMode(new SelectMembersEndMode(e.getX(), e.getY()));

            } else if (!(closest instanceof PrebuiltJoint)) {
                bridgeCanvas.selected.add(closest);
                setMode(new SelectJointEndMode(closest));
            }
        }
    }
    class SelectJointEndMode extends MouseAdapter implements Mode {
        private Joint startJoint;
        SelectJointEndMode(Joint startJoint) {
            this.startJoint = startJoint;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            bridgeCanvas.selected.add(startJoint);
            preview = new JointPreview(e.getX(), e.getY(), bridgeCanvas);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            bridge.moveJoint(startJoint, e.getX(), e.getY());
            preview = null;
            setMode(new SelectStartMode());
        }
    }
    class SelectMembersEndMode extends MouseAdapter implements Mode {
        private int x, y;
        SelectMembersEndMode(int x, int y) {
            this.x = x;
            this.y = y;
            bridgeCanvas.selected.clear();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x2 = e.getX();
            int y2 = e.getY();
            int previewX = Math.min(x, x2);
            int previewY = Math.min(y, y2);
            int width = Math.abs(x - x2);
            int height = Math.abs(y - y2);
            preview = new SelectPreview(previewX, previewY, width, height);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int x2 = e.getX();
            int y2 = e.getY();
            int previewX = Math.min(x, x2);
            int previewY = Math.min(y, y2);
            int width = Math.abs(x - x2);
            int height = Math.abs(y - y2);
            bridgeCanvas.selected.addAll(
                    bridge.getBoundedMembers(previewX, previewY, width, height)
            );
            preview = null;
            bridgeCanvas.requestFocusInWindow();
            setMode(new SelectStartMode());
        }
    }

    private Mode mode = new SelectStartMode();
    private Shape preview;

    // Adapted from PaintE.java
    private class Mouse extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent arg0) {
            mode.mouseMoved(arg0);
            bridgeCanvas.repaint();
        }
        @Override
        public void mousePressed(MouseEvent arg0) {
            mode.mousePressed(arg0);
            bridgeCanvas.repaint();
        }
        public void mouseDragged(MouseEvent arg0) {
            mode.mouseDragged(arg0);
            bridgeCanvas.repaint();
        }
        @Override
        public void mouseReleased(MouseEvent arg0) {
            mode.mouseReleased(arg0);
            bridgeCanvas.repaint();
        }
    }
    private final MainFrame frame;

    public GamePanel(MainFrame frame) {
        this.frame = frame;
        this.bridge = new Bridge();

        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(createMemberDetailsPanel(), BorderLayout.EAST);

        // BridgeCanvas & Modes adapted from PaintE.java
        this.bridgeCanvas = new BridgeCanvas();
        add(this.bridgeCanvas, BorderLayout.CENTER);

    }

    public JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(200, 200, 200));

        JButton placeJointButton = new JButton("Place Joint");
        JButton placeMemberButton = new JButton("Place Member");
        JButton selectToolButton = new JButton("Select Tool");
        JButton undoButton = new JButton("Undo");
        JButton testBridgeButton = new JButton("Test Bridge");

        placeJointButton.addActionListener(e -> setMode(new JointMode()));
        placeMemberButton.addActionListener(e -> setMode(new MemberStartMode()));
        selectToolButton.addActionListener(e -> setMode(new SelectStartMode()));
        undoButton.addActionListener(e -> {
            bridge.undo();
            repaint();
        });
        testBridgeButton.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "TODO: implement simulation of bridge")
        );

        toolbar.add(placeJointButton);
        toolbar.add(placeMemberButton);
        toolbar.add(selectToolButton);
        toolbar.add(undoButton);

        // Test Bridge Button in Top-Right Corner
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(testBridgeButton);
        return toolbar;
    }

    public JPanel createMemberDetailsPanel() {
        JPanel memberDetailsPanel = new JPanel();
        memberDetailsPanel.setLayout(new BoxLayout(memberDetailsPanel, BoxLayout.Y_AXIS));
        memberDetailsPanel.setPreferredSize(new Dimension(200, 0));
        memberDetailsPanel.setBorder(BorderFactory.createTitledBorder("Bridge Members"));

        JTextArea memberDetails = new JTextArea();
        memberDetails.setEditable(false);
        JScrollPane memberDetailsScroll = new JScrollPane(memberDetails);
        memberDetailsPanel.add(memberDetailsScroll);
        return memberDetailsPanel;
    }

    private class BridgeCanvas extends JPanel {
        private static final String BACKGROUND_IMAGE = "files/polybridge/background.png";
        private static BufferedImage backgroundImg;

        public static final int CANVAS_WIDTH = 600;
        public static final int CANVAS_HEIGHT = 400;

        private ArrayList<BridgeComponent> selected = new ArrayList<>();
        private ArrayList<BridgeComponent> highlighted = new ArrayList<>();


        public BridgeCanvas() {
            super();
            setBackground(Color.WHITE);
            try {
                backgroundImg = ImageIO.read(new File(BACKGROUND_IMAGE));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read BridgeCanvas BACKGROUND_IMAGE: " + e);
            }
            Mouse mouseListener = new Mouse();
            addMouseListener(mouseListener); // press/release events
            addMouseMotionListener(mouseListener); // dragged events

            // switching tools, delete functionality
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        bridge.deleteBridgeComponents(selected);
                        bridgeCanvas.repaint();
                        selected.clear();
                    }
                }
            });
            requestFocusInWindow();

        }
        @Override
        public void paintComponent(Graphics gc) {
            super.paintComponent(gc);
            List<BridgeComponent> toHighlight = new ArrayList<>(highlighted);
            toHighlight.addAll(selected);
            highlighted.clear();

            bridge.setHighlighted(toHighlight);

            // Draw Background
            if (backgroundImg != null) {
                gc.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            }

            for (BridgeComponent bc : bridge.getCompiledBridgeComponents()) {
                bc.draw(gc);
            }
            if (preview != null) {
                preview.draw(gc);
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
        }
    }

    private void setMode(Mode m) {
        // clear selected BridgeComponents if switching to non-select mode
        if (m.getClass() != SelectStartMode.class) {
            bridgeCanvas.selected.clear();
        }
        System.out.println("Mode updating to " + m);
        mode = m;
        preview = null;
        bridgeCanvas.repaint();
    }
}
