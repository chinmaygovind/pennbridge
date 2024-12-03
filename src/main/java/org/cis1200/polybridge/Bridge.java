package org.cis1200.polybridge;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Bridge {
    private List<Action> actions = new ArrayList<>();

    private List<BridgeComponent> compiledBridgeComponents = new ArrayList<>();
    private List<Joint> joints = new ArrayList<>();
    private List<Member> members = new ArrayList<>();

    interface Action {
        public List<BridgeComponent> getComponents();
        public void applyAction();
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
            System.out.printf("Updating joint from %s to (%d, %d)", oldJoint, newX, newY);
            for (BridgeComponent bc : compiledBridgeComponents) {
                if (bc.getClass() == Joint.class && bc.equals(oldJoint)) {
                    ((Joint) bc).move(newX, newY);
                }
            }
        }
    }
    class DeleteAction implements Action {
        List<BridgeComponent> bridgeComponents;

        public DeleteAction(List<BridgeComponent> bridgeComponents) {
            this.bridgeComponents = bridgeComponents;
        }
        public List<BridgeComponent> getComponents() {
            return bridgeComponents;
        }
        @Override
        public void applyAction() {
            compiledBridgeComponents.removeAll(bridgeComponents);
        }
    }

    public Bridge() {
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

    public void removeBridgeComponents(List<BridgeComponent> bridgeComponents) {
        Action delete = new DeleteAction(bridgeComponents);
        actions.add(delete);
        compileActions();
    }

    private void compileActions() {
        compiledBridgeComponents.clear();
        for (Action a : actions) {
            a.applyAction();
        }
        // remove members if their joints are missing
        compiledBridgeComponents.removeIf(bridgeComponent ->
                (bridgeComponent == null) ||
                (bridgeComponent.getClass() == Member.class &&
                        (((Member) bridgeComponent).getStart() == null ||
                        ((Member) bridgeComponent).getEnd() == null))
        );
        for (BridgeComponent bc : compiledBridgeComponents) {
            if (bc.getClass() == Joint.class) {
                joints.add((Joint) bc);
            } else if (bc.getClass() == Member.class) {
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
                    m.getEnd().getY() > y && m.getEnd().getY() < y + height
            ) {
                members.add(m);
            }
        }
        return members;
    }
    public List<BridgeComponent> getCompiledBridgeComponents() {
        this.compileActions();
        return compiledBridgeComponents;
    }

}

