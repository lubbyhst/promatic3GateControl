package com.github.lubbyhst.dto;

public class DHT22Result {

    private final float humidity;
    private final float temperature;

    public DHT22Result(float humidity, float temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getTemperatureInF() {
        return temperature * 1.8f + 32;
    }
}
