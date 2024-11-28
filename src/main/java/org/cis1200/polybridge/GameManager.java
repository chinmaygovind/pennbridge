package org.cis1200.polybridge;

public class GameManager {
    private Bridge bridge;
    private boolean simulationSuccess;

    public GameManager() {
        bridge = new Bridge();
    }

    public void resetGame() {
        bridge = new Bridge();
        simulationSuccess = false;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public boolean isSimulationSuccess() {
        return simulationSuccess;
    }

    public void simulate() {
        simulationSuccess = bridge.simulate();
    }
}

