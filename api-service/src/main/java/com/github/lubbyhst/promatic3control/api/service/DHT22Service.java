package com.github.lubbyhst.promatic3control.api.service;

import java.time.Duration;

import com.github.lubbyhst.promatic3control.api.components.GatePinConfiguration;
import com.github.lubbyhst.promatic3control.api.dto.DHT22Result;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.promatic3control.api.gpio.sensors.DHT22;

@Service
public class DHT22Service {

    private static final int sensorReadTimeoutSec = 30;
    private static final int sensorPollingMillis = 500;

    @Autowired
    private GatePinConfiguration gatePinConfiguration;

    private DHT22Result indoorSensorData;
    private DHT22Result outdoorSensorData;

    public synchronized DHT22Result getDataFromOutdoorSensor() {
        if (outdoorSensorData == null) {
            readSensorData();
        }
        return outdoorSensorData;
    }

    public synchronized DHT22Result getDataFromIndoorSensor() {
        if (indoorSensorData == null) {
            readSensorData();
        }
        return indoorSensorData;
    }

    private DHT22Result readDataFromSensor(final int readingPin, final DHT22Result dht22Result) {
        return new FluentWait<>(new DHT22()).pollingEvery(Duration.ofMillis(sensorPollingMillis))
                .withTimeout(Duration.ofSeconds(sensorReadTimeoutSec)).until(dht22 -> dht22.read(readingPin, dht22Result));
    }

    public void readSensorData() {
        try {
            indoorSensorData = readDataFromSensor(gatePinConfiguration.getHumidityIndoorPinReading(), this.indoorSensorData);
        } catch (final TimeoutException ex) {
            //reset old data, in case sensor reading was not correct
            indoorSensorData = null;
        }
        try {
            outdoorSensorData = readDataFromSensor(gatePinConfiguration.getHumidityOutdoorPinReading(), this.outdoorSensorData);
        } catch (final TimeoutException ex) {
            //reset old data, in case sensor reading was not correct
            outdoorSensorData = null;
        }
    }

}
