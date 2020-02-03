package com.github.lubbyhst.enums;

public enum GateStatus {
    OPEN("Open", 1d), CLOSED("Closed", 0d), VENTILATION("Ventilaiton", 0.2d);

    private final String label;
    private final double numericStatus;

    GateStatus(final String label, final double numericStatus) {
        this.label = label;
        this.numericStatus = numericStatus;
    }

    public String getLabel() {
        return label;
    }

    public double getNumericStatus() {
        return numericStatus;
    }
}
