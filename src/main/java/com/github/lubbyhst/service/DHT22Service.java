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

    public DHT22Result getDataFromOutdoorSensor(){
        return readDataFromSensor(gatePinConfiguration.getHumidityOutdoorPinReading());
    }

    public DHT22Result getDataFromIndoorSensor(){
        return readDataFromSensor(gatePinConfiguration.getHumidityIndoorPinReading());
    }

    private DHT22Result readDataFromSensor(final int readingPin) {
        return new FluentWait<>(new DHT22()).pollingEvery(Duration.ofMillis(sensorPollingMillis))
                .withTimeout(Duration.ofSeconds(sensorReadTimeoutSec))
                .until(dht22 -> dht22.read(readingPin));
    }

}
