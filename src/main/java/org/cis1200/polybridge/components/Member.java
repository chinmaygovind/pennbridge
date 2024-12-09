package org.cis1200.polybridge.components;

import java.awt.*;
import java.util.Objects;

public class Member extends BridgeComponent {
    private Joint start, end;
    private double force;
    private double maxForce;
    private Color color;
    private int number;

    public Member(Joint start, Joint end) {
        this.start = start;
        this.end = end;
        this.force = 0;
        this.color = Color.GRAY;
        this.number = 0;
    }

    public Joint getStart() {
        return start;
    }

    public Joint getEnd() {
        return end;
    }

    public enum Material {
        STEEL
    }

    public Material getMaterial() {
        return Material.STEEL;
    }

    public double getLength() {
        return Math.sqrt(
                Math.pow(getEnd().getX() - getStart().getX(), 2) +
                        Math.pow(getEnd().getY() - getStart().getY(), 2)
        );
    }

    public void setForce(double force) {
        this.force = force;
        // https://en.wikipedia.org/wiki/Euler%27s_critical_load
        this.maxForce = calculateMaxForce(); // some arbitrary made-up calculation :)
        // set color
        double r = 128;
        double g = 128;
        double b = 128;
        double toAdd = 128 * (force / 200);
        // tension positive blue, compression negative red
        if (toAdd > 0) {
            r = Math.max(0, Math.min(255, r - toAdd));
            g = Math.max(0, Math.min(255, g - toAdd));
            b = Math.max(0, Math.min(255, b + toAdd));
        } else {
            toAdd = -toAdd;
            r = Math.max(0, Math.min(255, r + toAdd));
            g = Math.max(0, Math.min(255, g - toAdd));
            b = Math.max(0, Math.min(255, b - toAdd));
        }
        setColor(new Color((int) r, (int) g, (int) b));
    }

    public double calculateMaxForce() {
        return 1_000;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public double getForce() {
        return force;
    }

    public boolean isBroken() {
        return Math.abs(force) > maxForce;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public Color getColor() {
        return color;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void draw(Graphics gc0) {
        Graphics2D gc = (Graphics2D) gc0;
        gc.setColor(getColor());
        if (highlighted) {
            gc.setColor(Color.CYAN);
        }
        if (isBroken()) {
            gc.setStroke(
                    new BasicStroke(
                            3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                            10.0f, new float[] { 10.0f }, 0.0f
                    )
            );
        } else {
            gc.setStroke(new BasicStroke(3));
        }
        gc.drawLine(getStart().getX(), getStart().getY(), getEnd().getX(), getEnd().getY());
        int midX = (getStart().getX() + getEnd().getX()) / 2;
        int midY = (getStart().getY() + getEnd().getY()) / 2;
        gc.setColor(Color.CYAN);
        gc.fillRect(midX - 7, midY - 7, 14, 14);
        gc.setStroke(new BasicStroke(1));
        gc.setColor(Color.BLACK);
        gc.drawRect(midX - 7, midY - 7, 14, 14);
        gc.setFont(new Font("Calibri", Font.PLAIN, 10));
        gc.drawString(String.valueOf(getNumber()), midX - 4, midY + 4);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return Objects.equals(start, member.start) && Objects.equals(end, member.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return String.format("Member | Start: " + getStart() + ", End: " + getEnd());
    }
}
