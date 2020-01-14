package com.github.lubbyhst.dto;

public class DHT22Result {

    private final float humidityRelative;
    private final float temperature;

    public DHT22Result(final float humidityRelative, final float temperature) {
        this.humidityRelative = humidityRelative;
        this.temperature = temperature;
    }

    public float getHumidityRelative() {
        return humidityRelative;
    }

    public double getHumidityAbsolute() {
        return calculateAbsoluteHumidity();
    }

    public double getDewPoint() {
        return calculateDewPoint();
    }

    public float getTemperature() {
        return temperature;
    }

    public float getTemperatureInF() {
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
}
