package org.cis1200.pennbridge.bridge;

import org.cis1200.pennbridge.truss.Truss;
import org.cis1200.pennbridge.truss.Vec2D;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Bridge {
    private LinkedList<Action> actions = new LinkedList<>();

    private List<BridgeComponent> compiledBridgeComponents = new ArrayList<>();
    private List<BridgeComponent> deletedBridgeComponents = new ArrayList<>();

    private List<Joint> deckJoints = new ArrayList<>();
    private List<Joint> joints = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private double[] solvedMemberForces;

    public static final int BRIDGE_LEFT_X = 100;
    public static final int BRIDGE_RIGHT_X = 500;

    interface Action {
        public List<BridgeComponent> getComponents();

        public void applyAction();

        public String toString();
    }

    class AddAction implements Action {
        List<BridgeComponent> bridgeComponents;

        public AddAction(List<BridgeComponent> bridgeComponents) {
            this.bridgeComponents = bridgeComponents;
        }

        public List<BridgeComponent> getComponents() {
            return bridgeComponents;
        }

        @Override
        public void applyAction() {
            compiledBridgeComponents.addAll(bridgeComponents);
        }

        @Override
        public String toString() {
            return "Adding " + getComponents();
        }
    }

    class MoveJointAction implements Action {
        Joint oldJoint;
        int newX, newY;

        public MoveJointAction(Joint oldJoint, int newX, int newY) {
            this.oldJoint = oldJoint;
            this.newX = newX;
            this.newY = newY;
        }

        public List<BridgeComponent> getComponents() {
            return Collections.singletonList(oldJoint);
        }

        @Override
        public void applyAction() {
            // System.out.printf("Updating joint from %s to (%d, %d)\n", oldJoint, newX,
            // newY);
            for (BridgeComponent bc : compiledBridgeComponents) {
                if (bc.getClass() == Joint.class && bc.equals(oldJoint)) {
                    ((Joint) bc).move(newX, newY);
                    return;
                }
            }
        }

        @Override
        public String toString() {
            return String.format("Updating joint from %s to (%d, %d)", oldJoint, newX, newY);
        }
    }

    class DeleteAction implements Action {
        List<BridgeComponent> bridgeComponents;

        public DeleteAction(List<BridgeComponent> bridgeComponents) {
            this.bridgeComponents = new ArrayList<>(bridgeComponents);
        }

        public List<BridgeComponent> getComponents() {
            return bridgeComponents;
        }

        @Override
        public void applyAction() {
            deletedBridgeComponents.addAll(bridgeComponents);
        }

        @Override
        public String toString() {
            return "Deleting " + getComponents();
        }
    }

    public Bridge() {
        this(Collections.emptyList());
    }

    public Bridge(List<BridgeComponent> bridgeComponents) {
        addPrebuiltComponents(bridgeComponents);
    }

    public void addPrebuiltComponents(List<BridgeComponent> bridgeComponents) {
        List<BridgeComponent> bcList = new ArrayList<>(PrebuiltJoint.BRIDGE_BUILTINS);
        for (BridgeComponent bc : bcList) {
            deckJoints.add((Joint) bc);
        }
        bcList.addAll(bridgeComponents);
        Action add = new AddAction(bcList);
        actions.add(add);
        compileActions();
    }

    public void addJoint(int x, int y) {
        Joint joint = new Joint(x, y);
        if (!joints.contains(joint)) {
            Action add = new AddAction(Collections.singletonList(joint));
            actions.add(add);
            compileActions();
        }
    }

    public void moveJoint(Joint oldJoint, int newX, int newY) {
        if (oldJoint.getPoint().equals(Joint.getSnappedPoint(newX, newY))) {
            return;
        }
        if (!joints.contains(oldJoint)) {
            throw new IllegalArgumentException("Tried to move Joint not in Bridge's joints");
        }
        Point newPoint = Joint.getSnappedPoint(newX, newY);
        Action move = new MoveJointAction(oldJoint, (int) newPoint.getX(), (int) newPoint.getY());
        actions.add(move);
        compileActions();
    }

    public void addMember(Joint start, Joint end) {
        Member member = new Member(start, end);
        if (!members.contains(member)) {
            Action add = new AddAction(Collections.singletonList(member));
            actions.add(add);
            compileActions();
        }
    }

    public void deleteBridgeComponents(List<BridgeComponent> bridgeComponents) {
        if (!bridgeComponents.isEmpty()) {
            Action delete = new DeleteAction(bridgeComponents);
            actions.add(delete);
            compileActions();
        }
    }

    public void undo() {
        if (actions.size() > 1) {
            actions.removeLast();
            compileActions();
        }
    }

    private void compileActions() {
        compiledBridgeComponents.clear();
        deletedBridgeComponents.clear();
        // System.out.println("-----------COMPILING BRIDGE----------------");
        for (Action a : actions) {
            a.applyAction();
        }
        for (BridgeComponent bc : deletedBridgeComponents) {
            compiledBridgeComponents.remove(bc);
        }
        // remove members if their joints are missing
        compiledBridgeComponents.removeIf(
                bridgeComponent -> (bridgeComponent == null) ||
                        (bridgeComponent instanceof Member &&
                                (((Member) bridgeComponent).getStart() == null ||
                                        ((Member) bridgeComponent).getEnd() == null))
        );

        joints.clear();
        members.clear();
        for (BridgeComponent bc : compiledBridgeComponents) {
            if (bc instanceof Joint) {
                joints.add((Joint) bc);
            } else if (bc instanceof Member) {
                members.add((Member) bc);
            }
        }
    }

    public List<Joint> getJoints() {
        return joints;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setHighlighted(List<BridgeComponent> toHighlight) {
        for (BridgeComponent bc : compiledBridgeComponents) {
            bc.setHighlighted(toHighlight.contains(bc));
        }
    }

    public Joint getClosestJoint(int x, int y, int maxTolerance) {
        Point target = new Point(x, y);
        Joint closest = null;
        double distance = maxTolerance; // max tolerance for closest
        for (Joint j : getJoints()) {
            if (target.distance(j.getPoint()) < distance) {
                closest = j;
                distance = target.distance(j.getPoint());
            }
        }
        return closest;
    }

    public List<Member> getBoundedMembers(int x, int y, int width, int height) {
        List<Member> members = new ArrayList<>();
        for (Member m : getMembers()) {
            if (m.getStart().getX() > x && m.getStart().getX() < x + width &&
                    m.getEnd().getX() > x && m.getEnd().getX() < x + width &&
                    m.getStart().getY() > y && m.getStart().getY() < y + height &&
                    m.getEnd().getY() > y && m.getEnd().getY() < y + height) {
                members.add(m);
            }
        }
        return members;
    }

    public List<BridgeComponent> getCompiledBridgeComponents() {
        return compiledBridgeComponents;
    }

    public boolean isRigid() {
        // 2 * (# of joints) = (# of reaction forces) + (# of members)
        // 2 * # of joints = 3 + # of members
        compileActions();
        return 2 * joints.size() == 3 + members.size();
    }

    public Truss getTruss() {
        return new Truss(deckJoints.get(0), deckJoints.get(deckJoints.size() - 1), joints, members);
    }

    public boolean solveMemberForces(double loadX, double loadForce) {
        Truss t = getTruss();
        Vec2D loadPoint = Truss.toTrussCoords(loadX, Truss.SWING_ORIGIN_Y);
        if (loadPoint.getX() < Truss.LEFT_ANCHOR_X || loadPoint.getX() >= Truss.RIGHT_ANCHOR_X) {
            throw new IllegalArgumentException(
                    String.format("%f is invalid loadX position", loadX)
            );
        }
        int leftJointNum = (int) (loadPoint.getX() / 4);
        int rightJointNum = leftJointNum + 1;
        Joint leftJoint = deckJoints.get(leftJointNum);
        Joint rightJoint = deckJoints.get(rightJointNum);
        double leftJointDistance = loadPoint.getX() - 4 * leftJointNum; // distance (m) from
                                                                        // leftJoint
        double totalDistance = 4;
        double leftJointLoad = -loadForce * (totalDistance - leftJointDistance) / totalDistance;
        double rightJointLoad = -loadForce * (leftJointDistance) / totalDistance;
        t.applyLoad(leftJoint, leftJointLoad);
        t.applyLoad(rightJoint, rightJointLoad);
        double[] memberForces = t.solveMemberForces();
        this.solvedMemberForces = memberForces;
        boolean intact = true;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setForce(memberForces[i]);
            intact &= !members.get(i).isBroken();
        }
        return intact;
    }

    public double[] getSolvedMemberForces() {
        return solvedMemberForces;
    }

    public double getCost() {
        double total = 0;
        for (Member m : getMembers()) {
            total += m.getLength();
        }
        return 10 * total;
    }

    public void saveToFile(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(this.toString());
        writer.close();
    }

    public static Bridge loadFromFile(File file) throws IOException {
        if (!file.getName().endsWith(".bridge")) {
            throw new FileNotFoundException(
                    "Invalid file type. File must end in \".bridge\" extension."
            );
        }
        return loadBridge(new BufferedReader(new FileReader(file)));
    }

    public static Bridge loadFromString(String str) throws IllegalArgumentException {
        try {
            return loadBridge(new BufferedReader(new StringReader(str)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Bridge string is in invalid format!");
        }
    }

    public static Bridge loadBridge(BufferedReader reader) throws IOException {
        List<Joint> joints = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        for (String line : reader.lines().toList()) {
            String[] lineData = line.split(",");
            try {
                if (lineData[0].equals("joint")) {
                    if (lineData[3].equals("false")) {
                        joints.add(
                                new Joint(
                                        Integer.parseInt(lineData[1]),
                                        Integer.parseInt(lineData[2])
                                )
                        );
                    } else if (lineData[3].equals("true")) {
                        joints.add(
                                new PrebuiltJoint(
                                        Integer.parseInt(lineData[1]),
                                        Integer.parseInt(lineData[2])
                                )
                        );
                    }
                } else if (lineData[0].equals("member")) {
                    members.add(
                            new Member(
                                    joints.get(Integer.parseInt(lineData[1])),
                                    joints.get(Integer.parseInt(lineData[2]))
                            )
                    );
                }
            } catch (Exception e) {
                System.out.println(e);
                throw new IOException("Error occurred while parsing bridge file: " + e);
            }
        }
        joints.removeIf(
                joint -> joint instanceof PrebuiltJoint
        );
        List<BridgeComponent> bridgeComponents = new ArrayList<>();
        bridgeComponents.addAll(joints);
        bridgeComponents.addAll(members);
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setNumber(i + 1);
        }
        return new Bridge(bridgeComponents);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Joint> joints = getJoints();
        List<Member> members = getMembers();
        for (Joint j : joints) {
            boolean preBuilt = j instanceof PrebuiltJoint;
            sb.append(
                    String.format("joint,%d,%d,%b\n", j.getX(), j.getY(), preBuilt)
            );
        }
        for (Member m : members) {
            int startID = joints.indexOf(m.getStart());
            int endID = joints.indexOf(m.getEnd());
            sb.append(
                    String.format("member,%d,%d\n", startID, endID)
            );
        }
        return sb.toString().trim();
    }

}
