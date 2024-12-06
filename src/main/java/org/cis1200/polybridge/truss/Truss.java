package org.cis1200.polybridge.truss;

import org.cis1200.polybridge.components.Joint;
import org.cis1200.polybridge.components.Member;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// https://engineeringstatics.org/Chapter_06.html
public class Truss {
    private double[][] forceMatrix;

    private TrussJoint leftAnchor; // assumed pinned
    private TrussJoint rightAnchor; // assumed roller

    private List<TrussJoint> joints;
    private List<TrussMember> members;

    public Truss(Joint leftAnchor, Joint rightAnchor, List<Joint> joints, List<Member> members) {
        if (!joints.contains(leftAnchor)) {
            throw new IllegalArgumentException("Left Anchor must be contained in joints list!");
        }
        if (joints.size() * 2 < 3 + members.size()) {
            throw new IndeterminateTrussException();
        }
        if (joints.size() * 2 > 3 + members.size()) {
            throw new UnstableTrussException();
        }
        if (leftAnchor.getY() != 0) {
            throw new IllegalArgumentException("Left anchor must be at y-level 0!");
        }
        if (rightAnchor.getY() != 0) {
            throw new IllegalArgumentException("Right anchor must be at y-level 0!");
        }
        this.joints = new ArrayList<>();
        for (Joint j : joints) {
            this.joints.add(new TrussJoint(j));
        }
        this.members = new ArrayList<>();
        for (Member m : members) {
            TrussJoint newStart = this.joints.get(joints.indexOf(m.getStart()));
            TrussJoint newEnd = this.joints.get(joints.indexOf(m.getEnd()));
            this.members.add(new TrussMember(newStart, newEnd, m));
        }
        // this.forceMatrix = getForceMatrix();
    }

    public Truss(TrussJoint leftAnchor, TrussJoint rightAnchor, List<TrussJoint> joints, List<TrussMember> members) {
        if (!joints.contains(leftAnchor)) {
            throw new IllegalArgumentException("Left Anchor must be contained in joints list!");
        }
        if (!joints.contains(rightAnchor)) {
            throw new IllegalArgumentException("Right Anchor must be contained in joints list!");
        }
        if (joints.size() * 2 < 3 + members.size()) {
            throw new IndeterminateTrussException();
        }
        if (joints.size() * 2 > 3 + members.size()) {
            throw new UnstableTrussException();
        }
        this.leftAnchor = leftAnchor;
        this.rightAnchor = rightAnchor;
        if (leftAnchor.getY() != 0) {
            throw new IllegalArgumentException("Left anchor must be at y-level 0!");
        }
        if (rightAnchor.getY() != 0) {
            throw new IllegalArgumentException("Right anchor must be at y-level 0!");
        }
        this.joints = joints;
        this.members = members;
        // this.forceMatrix = getForceMatrix();
    }

    public void applyLoad(Joint j, double load) {
        TrussJoint tj = new TrussJoint(j);
        if (!joints.contains(tj)) {
            throw new IllegalArgumentException("Cannot apply load to joint " + j + " not in truss!");
        }
        if (tj.getY() != leftAnchor.getY()) {
            throw new IllegalArgumentException("Cannot apply load to joint " + j +
                    " not on same y-level as anchor!"); // because chinmay is lazy and bad at soh cah toa
        }
        // assume load can only go vertically
        joints.get(joints.indexOf(tj)).setReactionForce(new Vec2D(0, -load));
    }

    // TODO: unfuck this up
    public double[][] getForceMatrix() {
        // 2 equations for each joint, each equation has 1 term per member + constant term
        double[][] forceMatrix = new double[2 * joints.size()][members.size()+1];
        // solve reaction forces
        // set leftAnchor as origin (0, 0)
        double length = rightAnchor.getX() - leftAnchor.getX();

        double totalLoadY = 0;
        double totalLoadTorque = 0;
        for (TrussJoint j : joints) {
            // torque = F * d sinTheta
            totalLoadY += j.getLoadForce().getY();
            double dX = j.getX() - leftAnchor.getX();
            double dY = j.getY() - leftAnchor.getY();
            double angle = Math.PI/2 - Math.atan2(dY, dX);
            double distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
            totalLoadTorque += j.getLoadForce().getY() * distance * Math.sin(angle);
        }
        // System.out.println("Total load torque: " + totalLoadTorque);
        //right anchor (assumed roller) upward reaction force must balance out other torques
        // recall F = torque / d
        double rightAnchorRy = -totalLoadTorque / length;
        // System.out.println("Right anchor Ry: " + rightAnchorRy);
        rightAnchor.setReactionForce(new Vec2D(0, rightAnchorRy));
        // leftAnchorRy + rightAnchorRy + totalLoadTorque = 0
        double leftAnchorRy = -rightAnchorRy - totalLoadY;
        leftAnchor.setReactionForce(new Vec2D(0, leftAnchorRy));
        // now that my reaction forces are solved for,
        // I can fill these in the force matrix's rightmost column
        for (int i = 0; i < joints.size(); i++) {
            TrussJoint j = joints.get(i);
            forceMatrix[2*i+1][forceMatrix[i].length-1] = j.getExternalForce().getY();
        }
        // hard part: i fill in rest of force matrix
        for (int j = 0; j < joints.size(); j++) {
            TrussJoint joint = joints.get(j);
            HashMap<TrussMember, Vec2D> connectedMembers = joint.getConnectedMembers();
            for (TrussMember member : connectedMembers.keySet()) {
                int m = members.indexOf(member);
                double angle = connectedMembers.get(member).getAngle();
                forceMatrix[2*j][m] = Math.cos(angle); // x
                forceMatrix[2*j+1][m] = Math.sin(angle); // y
            }
        }
        return forceMatrix;
    }

    public double[] solveMemberForces() {
        double[][] forceMatrix = getForceMatrix();
        // System.out.println("--- force matrix ---");
        // printMatrix(forceMatrix);
        double[][] matrix = new double[forceMatrix.length][forceMatrix[0].length];
        double[] memberForces = new double[forceMatrix[0].length-1];
        for (int i = 0; i < forceMatrix.length; i++) {
            for (int j = 0; j < forceMatrix[0].length; j++) {
                matrix[i][j] = forceMatrix[i][j];
            }
        }
        // rref
        //convert to echelon form
        for (int rowToSubtract = 0; rowToSubtract < matrix[0].length; rowToSubtract++) {
            double[] toSubtract = matrix[rowToSubtract];
            double firstTermToSubtract = toSubtract[rowToSubtract];
            //zero term will cause problems
            if (Math.abs(firstTermToSubtract) < 0.001) {
                // find new row to sub in
                for (int newRow = rowToSubtract+1; newRow < matrix.length; newRow++) {
                    if (Math.abs(matrix[newRow][rowToSubtract]) > 0.001) {
                        double[] temp = Arrays.copyOf(matrix[rowToSubtract], matrix[newRow].length);
                        matrix[rowToSubtract] =  Arrays.copyOf(matrix[newRow], matrix[newRow].length);
                        toSubtract =  Arrays.copyOf(matrix[newRow], matrix[newRow].length);
                        matrix[newRow] = temp;
                        firstTermToSubtract = toSubtract[rowToSubtract];
                        // System.out.printf("---- swapped rows %d and %d----\n", rowToSubtract, newRow);
                        // printMatrix(matrix);
                        break;
                    }
                }
            }
            for (int row = rowToSubtract+1; row < matrix.length; row++) {
                double firstTerm = matrix[row][Math.min(rowToSubtract, matrix[row].length-1)];
                double scale = firstTerm / firstTermToSubtract;
                for (int i = 0; i < toSubtract.length; i++) {
                    matrix[row][i] -= scale * toSubtract[i];
                }
            }
            // System.out.printf("-------------subtracted row %d-------------\n", rowToSubtract);
            // printMatrix(matrix);
        }
        //convert to diagonal form
        for (int rowToSubtract = matrix[0].length-2; rowToSubtract >= 0; rowToSubtract--) {
            double[] toSubtract = matrix[rowToSubtract];
            double firstTermToSubtract = toSubtract[rowToSubtract];
            for (int row = rowToSubtract-1; row >= 0; row--) {
                double firstTerm = matrix[row][Math.min(rowToSubtract, matrix[row].length-1)];
                double scale = firstTerm / firstTermToSubtract;
                for (int i = 0; i < toSubtract.length; i++) {
                    matrix[row][i] -= scale * toSubtract[i];
                }
            }
            // System.out.println("-------------------------------");
            // printMatrix(matrix);
        }
        //scale diagonals to rref form
        for (int row = 0; row < matrix[0].length-1; row++) {
            double coeff = matrix[row][row];
            // negated for tension/compression
            memberForces[row] = -matrix[row][matrix[row].length-1] / coeff;
        }
        return memberForces;
    }

    public void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.print("[");
            for (double element : row) {
                System.out.printf("%.3f\t\t", element);
            }
            System.out.println("]");
        }
    }
    public List<TrussJoint> getJoints() {
        return joints;
    }

    public List<TrussMember> getMembers() {
        return members;
    }

    /**
     * The truss calculations use a different coordinate system.
     * The origin is at the left support joint of the bridge, with y increasing upward and x increasing rightward.
     * The bridge is exactly 20m wide.
     * @param p Point2D of the coordinates in the Swing coordinate format.
     * @return a Vec2D of the points in the truss coordinate system.
     */
    public static Vec2D toTrussCoords(Point2D p) {
        double newX = (p.getX() - 100) / 25.0; // x = 0 is at 100px, x = 20 is at 500px
        double newY = -(p.getY() - 500) / 25.0; // y = 0 is at 500px, flip upside down so it increases upward
        return new Vec2D(newX, newY);
    }

    public static Vec2D toTrussCoords(double x, double y) {
        double newX = (x - 100) / 25.0; // x = 0 is at 100px, x = 20 is at 500px
        double newY = -(y - 500) / 25.0; // y = 0 is at 500px, flip upside down so it increases upward
        return new Vec2D(newX, newY);
    }
}
