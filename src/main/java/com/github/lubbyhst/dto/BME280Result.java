package com.github.lubbyhst.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BME280Result {

    private final double humidityRelative;
    private final double temperature;
    private final double pressure;

    public BME280Result(final double humidityRelative, final double temperature, final double pressure) {
        this.humidityRelative = new BigDecimal(humidityRelative).setScale(1, RoundingMode.HALF_UP).doubleValue();
        this.temperature = new BigDecimal(temperature).setScale(1, RoundingMode.HALF_UP).doubleValue();
        this.pressure = pressure;
    }

    public double getHumidityRelative() {
        return humidityRelative;
    }

    public double getHumidityAbsolute() {
        return new BigDecimal(calculateAbsoluteHumidity()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getDewPoint() {
        return calculateDewPoint();
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperatureInF() {
        return temperature * 1.8f + 32;
    }

    private double calculateAbsoluteHumidity() {
        return ((0.000002 * Math.pow(temperature, 4.0)) + (0.0002 * Math.pow(temperature, 3.0)) + (0.0095 * Math.pow(temperature, 2.0)) + (
                0.337 * temperature) + 4.9034) * humidityRelative;
    }

    private double calculateDewPoint() {
        return 243.12 * ((17.62 * temperature) / (243.12 + temperature) + Math.log(humidityRelative / 100)) / (
                (17.62 * 243.12) / (243.12 + temperature) - Math.log(humidityRelative / 100));
    }

    public double getPressure() {
        return pressure;
    }
}
