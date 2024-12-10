package org.cis1200.pennbridge.truss;

public class IndeterminateTrussException extends IllegalArgumentException {
    public IndeterminateTrussException() {
        super("Too many members, truss is indeterminate!");
    }
}
