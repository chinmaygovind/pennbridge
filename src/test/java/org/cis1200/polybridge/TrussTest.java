package org.cis1200.polybridge;

import org.cis1200.polybridge.truss.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrussTest {

    @Test
    public void createTruss() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 8));
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        TrussMember memberBC = new TrussMember(jointB, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberBC)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        assertEquals(t.getJoints().size(), 3);
        assertEquals(t.getMembers().size(), 3);
    }

    @Test
    public void createUnstableTruss() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 8));
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC)
        );
        assertThrows(UnstableTrussException.class, () -> {
            Truss t = new Truss(jointA, jointB, joints, members);
        });
    }

    @Test
    public void createIndeterminateTruss() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 8));
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberAB, memberAC)
        );
        assertThrows(IndeterminateTrussException.class, () -> {
            Truss t = new Truss(jointA, jointB, joints, members);
        });
    }

    @Test
    public void checkReactionForces() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 5 * Math.sqrt(3)));
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        jointC.setLoadForce(new Vec2D(0, -100));
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        TrussMember memberBC = new TrussMember(jointB, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberBC)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[][] forceMatrix = t.getForceMatrix();
        assertEquals(forceMatrix[0][3], 0, 0.001);
        assertEquals(forceMatrix[1][3], 50, 0.001);
        assertEquals(forceMatrix[2][3], 0, 0.001);
        assertEquals(forceMatrix[3][3], 50, 0.001);
        assertEquals(forceMatrix[4][3], 0, 0.001);
        assertEquals(forceMatrix[5][3], -100, 0.001);

    }

    @Test
    public void checkReactionForces2() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(8, 6));
        // joint c is skewed to the right, load is balanced asymmetrically
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        jointC.setLoadForce(new Vec2D(0, -100));
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        TrussMember memberBC = new TrussMember(jointB, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberBC)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[][] forceMatrix = t.getForceMatrix();
        assertEquals(forceMatrix[0][3], 0, 0.001);
        assertEquals(forceMatrix[1][3], 20, 0.001);
        assertEquals(forceMatrix[2][3], 0, 0.001);
        assertEquals(forceMatrix[3][3], 80, 0.001);
        assertEquals(forceMatrix[4][3], 0, 0.001);
        assertEquals(forceMatrix[5][3], -100, 0.001);

    }

    @Test
    public void checkMemberForces() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 5 * Math.sqrt(3)));
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        jointC.setLoadForce(new Vec2D(0, -100));
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        TrussMember memberBC = new TrussMember(jointB, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberBC)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[] memberForces = t.solveMemberForces();
        assertEquals(memberForces[0], 28.867513459481298, 0.001);
        assertEquals(memberForces[1], -57.73502691896258, 0.001);
        assertEquals(memberForces[2], -57.73502691896258, 0.001);

    }

    @Test
    public void checkMemberForces2() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(8, 6));
        // joint c is skewed to the right, load is balanced asymmetrically
        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC)
        );
        jointC.setLoadForce(new Vec2D(0, -100));
        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberAC = new TrussMember(jointA, jointC, null);
        TrussMember memberBC = new TrussMember(jointB, jointC, null);
        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberAC, memberBC)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[] memberForces = t.solveMemberForces();
        assertEquals(memberForces[0], 26.66666666666669, 0.001);
        assertEquals(memberForces[1], -33.33333333333336, 0.001);
        assertEquals(memberForces[2], -84.32740427115682, 0.001);

    }

    @Test
    public void checkMemberForces3() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 5));
        TrussJoint jointD = new TrussJoint(new Vec2D(3, 3));
        TrussJoint jointE = new TrussJoint(new Vec2D(7, 3));

        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC, jointD, jointE)
        );

        jointC.setLoadForce(new Vec2D(0, -200));

        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberCD = new TrussMember(jointD, jointC, null);
        // TrussMember memberBC = new TrussMember(jointB, jointC, null);
        TrussMember memberAD = new TrussMember(jointA, jointD, null);
        TrussMember memberAE = new TrussMember(jointA, jointE, null);
        TrussMember memberDE = new TrussMember(jointD, jointE, null);
        TrussMember memberBE = new TrussMember(jointB, jointE, null);
        TrussMember memberCE = new TrussMember(jointC, jointE, null);

        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberCD, memberAD, memberAE, memberDE, memberBE, memberCE)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[] memberForces = t.solveMemberForces();
        assertEquals(memberForces[0], 100, 0.001);
        assertEquals(memberForces[1], -141.4213562373095, 0.001);
        assertEquals(memberForces[2], -141.4213562373095, 0.001);
        assertEquals(memberForces[3], 0, 0.001);
        assertEquals(memberForces[4], 0, 0.001);
        assertEquals(memberForces[5], -141.4213562373095, 0.001);
        assertEquals(memberForces[6], -141.4213562373095, 0.001);
    }

    @Test
    public void checkMemberForcesMultiLoad() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 5));
        TrussJoint jointD = new TrussJoint(new Vec2D(3, 3));
        TrussJoint jointE = new TrussJoint(new Vec2D(7, 3));

        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC, jointD, jointE)
        );

        jointC.setLoadForce(new Vec2D(0, -200));
        jointD.setLoadForce(new Vec2D(0, -100));

        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberCD = new TrussMember(jointD, jointC, null);
        // TrussMember memberBC = new TrussMember(jointB, jointC, null);
        TrussMember memberAD = new TrussMember(jointA, jointD, null);
        TrussMember memberAE = new TrussMember(jointA, jointE, null);
        TrussMember memberDE = new TrussMember(jointD, jointE, null);
        TrussMember memberBE = new TrussMember(jointB, jointE, null);
        TrussMember memberCE = new TrussMember(jointC, jointE, null);

        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberCD, memberAD, memberAE, memberDE, memberBE, memberCE)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[] memberForces = t.solveMemberForces();
        assertEquals(memberForces[0], 130, 0.001);
        assertEquals(memberForces[1], -141.4213562373095, 0.001);
        assertEquals(memberForces[2], -282.8427124746191, 0.001);
        assertEquals(memberForces[3], 76.15773105863914, 0.001);
        assertEquals(memberForces[4], -100, 0.001);
        assertEquals(memberForces[5], -183.84776310850236, 0.001);
        assertEquals(memberForces[6], -141.4213562373095, 0.001);
    }

    @Test
    public void checkMemberForcesConsistent() {
        TrussJoint jointA = new TrussJoint(new Vec2D(0, 0));
        TrussJoint jointB = new TrussJoint(new Vec2D(10, 0));
        TrussJoint jointC = new TrussJoint(new Vec2D(5, 5));
        TrussJoint jointD = new TrussJoint(new Vec2D(3, 3));
        TrussJoint jointE = new TrussJoint(new Vec2D(7, 3));

        List<TrussJoint> joints = new ArrayList<>(
                List.of(jointA, jointB, jointC, jointD, jointE)
        );

        jointC.setLoadForce(new Vec2D(0, -200));

        TrussMember memberAB = new TrussMember(jointA, jointB, null);
        TrussMember memberCD = new TrussMember(jointD, jointC, null);
        // TrussMember memberBC = new TrussMember(jointB, jointC, null);
        TrussMember memberAD = new TrussMember(jointA, jointD, null);
        TrussMember memberAE = new TrussMember(jointA, jointE, null);
        TrussMember memberDE = new TrussMember(jointD, jointE, null);
        TrussMember memberBE = new TrussMember(jointB, jointE, null);
        TrussMember memberCE = new TrussMember(jointC, jointE, null);

        List<TrussMember> members = new ArrayList<>(
                List.of(memberAB, memberCD, memberAD, memberAE, memberDE, memberBE, memberCE)
        );

        Truss t = new Truss(jointA, jointB, joints, members);
        double[] memberForces = t.solveMemberForces();
        double[] memberForces2 = t.solveMemberForces();
        assertEquals(memberForces.length, memberForces2.length);
        for (int i = 0; i < memberForces.length; i++) {
            assertEquals(memberForces[i], memberForces2[i], 0.001);
        }
    }
}
