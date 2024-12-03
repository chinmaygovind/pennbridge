package org.cis1200.polybridge;

import java.awt.*;
import java.util.Objects;

public class Member extends BridgeComponent {
    private Joint start, end;

    public Member(Joint start, Joint end) {
        this.start = start;
        this.end = end;
    }

    public Joint getStart() {
        return start;
    }
    public Joint getEnd() {
        return end;
    }

    public void draw(Graphics gc0) {
        Graphics2D gc = (Graphics2D) gc0;
        gc.setColor(Color.GRAY);
        if (highlighted) {
            gc.setColor(Color.BLUE);
        }
        gc.setStroke(new BasicStroke(3));
        gc.drawLine(getStart().getX(), getStart().getY(), getEnd().getX(), getEnd().getY());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(start, member.start) && Objects.equals(end, member.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
