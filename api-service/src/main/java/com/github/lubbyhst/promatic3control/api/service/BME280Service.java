package com.github.lubbyhst.promatic3control.api.service;

import java.io.IOException;
import java.time.Duration;

import com.github.lubbyhst.promatic3control.api.dto.BME280Result;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.promatic3control.api.gpio.sensors.BME280;
import com.pi4j.io.i2c.I2CFactory;

@Service
public class BME280Service {

    private static final int sensorReadTimeoutSec = 30;
    private static final int sensorPollingMillis = 500;

    private BME280Result outdoorSensorData;

    public synchronized BME280Result getDataFromOutdoorSensor() {
        if (outdoorSensorData == null) {
            readSensorData();
        }
        return outdoorSensorData;
    }

    private BME280Result readDataFromSensor() {
        return new FluentWait<>(new BME280()).pollingEvery(Duration.ofMillis(sensorPollingMillis))
                .withTimeout(Duration.ofSeconds(sensorReadTimeoutSec)).until(bme280 -> {
                    try {
                        return bme280.pollBME280();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    } catch (final I2CFactory.UnsupportedBusNumberException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    public void readSensorData() {
        try {
            outdoorSensorData = readDataFromSensor();
        } catch (final TimeoutException ex) {
            //reset old data, in case sensor reading was not correct
            outdoorSensorData = null;
        }
    }

}
