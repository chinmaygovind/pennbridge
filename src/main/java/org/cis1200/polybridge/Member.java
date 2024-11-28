package org.cis1200.polybridge;

public class Member {
    private Joint start, end;

    public Member(Joint start, Joint end) {
        this.start = start;
        this.end = end;
    }

    public Joint getStart() { return start; }
    public Joint getEnd() { return end; }
}
