package org.cis1200.polybridge.truss;

import org.cis1200.polybridge.components.Member;

public class TrussMember {

    private TrussJoint start;
    private TrussJoint end;
    private Member member;
    private double load;

    public TrussMember(TrussJoint start, TrussJoint end, Member m) {
        this.start = start;
        this.end = end;
        this.member = m;// member has stopped sounding like a real word
        Vec2D start2End = new Vec2D(end.getX() - start.getX(), end.getY() - start.getY());
        Vec2D end2Start = new Vec2D(start.getX() - end.getX(), start.getY() - end.getY());
        start.addConnectedMember(this, start2End);
        end.addConnectedMember(this, end2Start);
    }

    public TrussJoint getStart() {
        return this.start;
    }

    public TrussJoint getEnd() {
        return this.end;
    }

    public double getLoad() {
        return load;
    }
}
