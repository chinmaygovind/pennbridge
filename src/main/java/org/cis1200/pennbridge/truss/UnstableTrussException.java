package org.cis1200.pennbridge.truss;

public class UnstableTrussException extends IllegalArgumentException {
    public UnstableTrussException() {
        super("Truss is unstable!");
    }
}
