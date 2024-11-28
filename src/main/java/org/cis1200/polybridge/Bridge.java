package org.cis1200.polybridge;

import java.util.ArrayList;
import java.util.List;

public class Bridge {
    private List<Joint> joints;
    private List<Member> members;

    public Bridge() {
        joints = new ArrayList<>();
        members = new ArrayList<>();
    }

    public void addNode(int x, int y) {
        joints.add(new Joint(x, y));
    }

    public List<Joint> getJoints() {
        return joints;
    }
    public void addSegment(Joint start, Joint end) {
        members.add(new Member(start, end));
    }

    public List<Member> getMembers() {
        return members;
    }
    public boolean simulate() {
        // Physics logic to determine if the bridge holds
        return true; // Placeholder
    }
}

