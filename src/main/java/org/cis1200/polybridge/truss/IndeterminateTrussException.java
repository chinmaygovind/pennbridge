package org.cis1200.polybridge.truss;

public class IndeterminateTrussException extends IllegalArgumentException {
    public IndeterminateTrussException() {
        super("Too many members, truss is indeterminate!");
    }
}
