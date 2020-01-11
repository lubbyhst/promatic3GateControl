package com.github.lubbyhst.service;

import java.time.Duration;

import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.components.GatePinConfiguration;
import com.github.lubbyhst.dto.DHT22Result;
import com.github.lubbyhst.gpio.sensors.DHT22;

@Service
public class DHT22Service {

    private static final int sensorReadTimeoutSec = 30;
    private static final int sensorPollingMillis = 500;

    @Autowired
    private GatePinConfiguration gatePinConfiguration;

    private DHT22Result indoorSensorData;
    private DHT22Result outdoorSensorData;

    public DHT22Result getDataFromOutdoorSensor(){
        if (outdoorSensorData == null) {
            readSensorData();
        }
        return outdoorSensorData;
    }

    public DHT22Result getDataFromIndoorSensor(){
        if (indoorSensorData == null) {
            readSensorData();
        }
        return indoorSensorData;
    }

    private DHT22Result readDataFromSensor(final int readingPin) {
        return new FluentWait<>(new DHT22()).pollingEvery(Duration.ofMillis(sensorPollingMillis))
                .withTimeout(Duration.ofSeconds(sensorReadTimeoutSec))
                .until(dht22 -> dht22.read(readingPin));
    }

    public void readSensorData() {
        indoorSensorData = readDataFromSensor(gatePinConfiguration.getHumidityIndoorPinReading());
        outdoorSensorData = readDataFromSensor(gatePinConfiguration.getHumidityOutdoorPinReading());
    }

}
