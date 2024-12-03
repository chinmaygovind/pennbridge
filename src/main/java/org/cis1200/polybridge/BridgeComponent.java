package org.cis1200.polybridge;

import javax.swing.*;
import java.awt.*;

public abstract class BridgeComponent extends Shape {
    boolean highlighted;
    void setHighlighted(boolean isHighlighted) {
        highlighted = isHighlighted;
    }
}
