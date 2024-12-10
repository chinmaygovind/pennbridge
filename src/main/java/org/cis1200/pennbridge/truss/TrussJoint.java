package org.cis1200.pennbridge.truss;

import org.cis1200.pennbridge.components.Joint;

import java.util.HashMap;
import java.util.Objects;

public class TrussJoint {
    private Vec2D position;
    private Vec2D externalForce;
    private Vec2D loadForce;
    private Vec2D reactionForce;
    private HashMap<TrussMember, Vec2D> connectedMembers;

    public TrussJoint(Vec2D position) {
        this.position = position;
        this.loadForce = new Vec2D(0, 0);
        this.reactionForce = new Vec2D(0, 0);
        this.externalForce = new Vec2D(0, 0);
        this.connectedMembers = new HashMap<>();
    }

    public TrussJoint(Joint j) {
        this(Truss.toTrussCoords(j.getPoint()));
    }

    public Vec2D getPosition() {
        return position;
    }

    public void addConnectedMember(TrussMember trussMember, Vec2D delta) {
        connectedMembers.put(trussMember, delta);
    }

    public HashMap<TrussMember, Vec2D> getConnectedMembers() {
        return connectedMembers;
    }

    public void setLoadForce(Vec2D loadForce) {
        this.loadForce = loadForce;
    }

    public Vec2D getLoadForce() {
        return loadForce;
    }

    public void setReactionForce(Vec2D reactionForce) {
        this.reactionForce = reactionForce;
    }

    public Vec2D getReactionForce() {
        return reactionForce;
    }

    public Vec2D getExternalForce() {
        externalForce = loadForce.add(reactionForce);
        return externalForce;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrussJoint that = (TrussJoint) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
