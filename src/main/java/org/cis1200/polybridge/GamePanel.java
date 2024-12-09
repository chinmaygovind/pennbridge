package org.cis1200.polybridge;

import org.cis1200.polybridge.components.*;
import org.cis1200.polybridge.components.Shape;
import org.cis1200.polybridge.components.Bridge;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    private Bridge bridge;
    private final BridgeCanvas bridgeCanvas;

    public static final String BACKGROUND_IMAGE = "files/polybridge/background.png";

    private final MemberDetailsPanel memberDetailsPanel;

    private static final int HIGHLIGHT_TOLERANCE = 20;

    interface Mode extends MouseListener, MouseMotionListener {
    }

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
            // System.out.println("Previewing joint at " + e.getX() + ", " + e.getY());
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
            memberDetailsPanel.updateMemberList();
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
        this.memberDetailsPanel = new MemberDetailsPanel();
        add(memberDetailsPanel, BorderLayout.EAST);

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
        JButton saveButton = new JButton("Save Bridge");
        JButton loadButton = new JButton("Load Bridge");

        placeJointButton.addActionListener(e -> setMode(new JointMode()));
        placeMemberButton.addActionListener(e -> setMode(new MemberStartMode()));
        selectToolButton.addActionListener(e -> setMode(new SelectStartMode()));
        undoButton.addActionListener(e -> {
            bridge.undo();
            memberDetailsPanel.updateMemberList();
            repaint();
        });

        final JFileChooser fc = new JFileChooser("files/polybridge/bridges");
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(
                new FileNameExtensionFilter("Bridge file", "bridge")
        );
        saveButton.addActionListener(
                e -> {
                    int returnVal = fc.showSaveDialog(GamePanel.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION &&
                            !fc.getSelectedFile().getName().isEmpty()) {
                        String fileName = fc.getCurrentDirectory().getAbsolutePath() +
                                File.separator +
                                fc.getSelectedFile().getName();
                        if (!fileName.endsWith(".bridge")) {
                            fileName += ".bridge";
                        }
                        try {
                            File toSave = new File(fileName);
                            bridge.saveToFile(toSave);
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Saved bridge to " + fileName,
                                    "Success",
                                    JOptionPane.PLAIN_MESSAGE
                            );
                        } catch (IOException | InvalidPathException ioException) {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Unable to save bridge to " + fileName,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
        );
        loadButton.addActionListener(
                e -> {
                    int returnVal = fc.showOpenDialog(GamePanel.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            bridge = Bridge.loadFromFile(file);
                            memberDetailsPanel.updateMemberList();
                            repaint();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Unable to load bridge from " + file.getName(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            System.out.println(ex);
                        }
                    }
                }
        );

        toolbar.add(placeJointButton);
        toolbar.add(placeMemberButton);
        toolbar.add(selectToolButton);
        toolbar.add(undoButton);

        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(saveButton);
        toolbar.add(loadButton);
        toolbar.add(testBridgeButton);
        return toolbar;
    }

    private class MemberDetailsPanel extends JPanel {

        private JTable memberDetails;
        private DefaultTableModel model;

        public MemberDetailsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(200, 0));
            setBorder(BorderFactory.createTitledBorder("Bridge Members"));
            model = new DefaultTableModel(
                    new String[] { "#", "Material", "Length", "Axial Force" }, 0
            );
            memberDetails = new JTable(model);
            memberDetails.getColumnModel().getColumn(0).setPreferredWidth(20);
            memberDetails.getColumnModel().getColumn(3).setPreferredWidth(120);
            JScrollPane memberDetailsScroll = new JScrollPane(memberDetails);
            add(memberDetailsScroll);
        }

        public void updateMemberList() {
            List<Member> members = bridge.getMembers();
            model.setRowCount(0);
            int memberID = 1;
            for (Member m : members) {
                m.setNumber(memberID);
                model.addRow(
                        new Object[] {
                            m.getNumber(),
                            m.getMaterial().toString(),
                            String.format("%.2f", m.getLength()),
                            String.format("%.2f", m.getForce())
                        }
                );
                memberID++;
            }
            // System.out.println(model.getRowCount() + "x" + model.getColumnCount());
            memberDetails.repaint();
        }
    }

    private class BridgeCanvas extends JPanel {
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
                        memberDetailsPanel.updateMemberList();
                        selected.clear();
                    }
                }
            });
            requestFocusInWindow();

        }

        public void simulate() {
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
        // System.out.println("Mode updating to " + m);
        mode = m;
        preview = null;
        bridgeCanvas.repaint();
    }
}
