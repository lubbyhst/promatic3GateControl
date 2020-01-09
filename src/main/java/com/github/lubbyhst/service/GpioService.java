package com.github.lubbyhst.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.gpio.sensors.DigitalInput;
import com.github.lubbyhst.gpio.sensors.DigitalOutput;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

@Service
public class GpioService{
    private static final Logger logger = Logger.getLogger(GpioService.class.getName());

    final GpioController gpioController;

    public GpioService(@Value("${pi4j.platform}")
    final String platform) throws PlatformAlreadyAssignedException {
        PlatformManager.setPlatform(Platform.valueOf(platform));
        this.gpioController = GpioFactory.getInstance();
    }

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("Init GpioService");
    }

    public DigitalInput getDigitalInput(final int pinAddress) {
        return new DigitalInput(gpioController, pinAddress);
    }

    public DigitalOutput getDigitalOutput(final int pinAddress) {
        return new DigitalOutput(gpioController, pinAddress);
    }
}
