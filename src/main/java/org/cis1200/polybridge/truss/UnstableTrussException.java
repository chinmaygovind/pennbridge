package org.cis1200.polybridge.truss;

public class UnstableTrussException extends IllegalArgumentException {
    public UnstableTrussException() {
        super("Truss is unstable!");
    }
}
