package com.github.lubbyhst.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatePinConfiguration {

    private final int humidityIndoorPinReading;

    private final int statusPinClose;
    private final int statusPinOpen;
    private final int statusPinVentilation;
    private final int statusPinMoving;

    private final int switchPinCloseOpen;
    private final int switchPinVentilation;

    public GatePinConfiguration(
            @Value("${gpio.gate.sensor.humidity.indoor.pin.reading}")
            final int humidityIndoorPinReading,
            @Value("${gpio.gate.sensor.status.pin.close}")
            final int statusPinClose,
            @Value("${gpio.gate.sensor.status.pin.open}")
            final int statusPinOpen,
            @Value("${gpio.gate.sensor.status.pin.ventilation}")
            final int statusPinVentilation,
            @Value("${gpio.gate.sensor.status.pin.moving}")
            final int statusPinMoving,
            @Value("${gpio.gate.switch.pin.close_open}")
            final int switchPinCloseOpen,
            @Value("${gpio.gate.switch.pin.ventilation}")
            final int switchPinVentilation) {
        this.humidityIndoorPinReading = humidityIndoorPinReading;
        this.statusPinClose = statusPinClose;
        this.statusPinOpen = statusPinOpen;
        this.statusPinVentilation = statusPinVentilation;
        this.statusPinMoving = statusPinMoving;
        this.switchPinCloseOpen = switchPinCloseOpen;
        this.switchPinVentilation = switchPinVentilation;
    }


    public int getHumidityIndoorPinReading() {
        return humidityIndoorPinReading;
    }
    
    public int getStatusPinClose() {
        return statusPinClose;
    }

    public int getStatusPinOpen() {
        return statusPinOpen;
    }

    public int getStatusPinVentilation() {
        return statusPinVentilation;
    }

    public int getStatusPinMoving() {
        return statusPinMoving;
    }

    public int getSwitchPinCloseOpen() {
        return switchPinCloseOpen;
    }

    public int getSwitchPinVentilation() {
        return switchPinVentilation;
    }
}
