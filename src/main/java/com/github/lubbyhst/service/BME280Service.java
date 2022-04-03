package com.github.lubbyhst.service;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.dto.BME280Result;
import com.github.lubbyhst.gpio.sensors.BME280;
import com.pi4j.io.i2c.I2CFactory;

@Service
public class BME280Service {
    private static final Logger logger = Logger.getLogger(BME280Service.class.getName());
    private static final int sensorReadTimeoutSec = 60;
    private static final int sensorPollingMillis = 1500;

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
                    } catch (final IOException | I2CFactory.UnsupportedBusNumberException e) {
                        logger.log(Level.WARNING, "Data reading failed." + e.getMessage());
                        logger.log(Level.FINE, e.getMessage(), e);
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
