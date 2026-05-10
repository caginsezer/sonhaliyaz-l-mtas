package com.example.ersimulator.service;

import org.springframework.stereotype.Service;

@Service
public class SimulationManager {

    private boolean isSimulationRunning = true;
    private int simulationSpeedMultiplier = 1;
    private boolean isQuarantineMode = false;

    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

    public void setSimulationRunning(boolean simulationRunning) {
        isSimulationRunning = simulationRunning;
    }

    public int getSimulationSpeedMultiplier() {
        return simulationSpeedMultiplier;
    }

    public void setSimulationSpeedMultiplier(int simulationSpeedMultiplier) {
        this.simulationSpeedMultiplier = simulationSpeedMultiplier;
    }

    public boolean isQuarantineMode() {
        return isQuarantineMode;
    }

    public void setQuarantineMode(boolean quarantineMode) {
        isQuarantineMode = quarantineMode;
    }
}
