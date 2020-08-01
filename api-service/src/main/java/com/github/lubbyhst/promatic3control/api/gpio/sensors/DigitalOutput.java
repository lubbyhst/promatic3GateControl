package com.github.lubbyhst.promatic3control.api.gpio.sensors;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class DigitalOutput {
    private static final Logger logger = Logger.getLogger(DigitalOutput.class.getName());

    private final Pin raspiPin;

    private final GpioPinDigitalOutput digitalOutput;

    public DigitalOutput(final GpioController gpioController, final int pinAddress) {
        this.raspiPin = RaspiPin.getPinByAddress(pinAddress);
        this.digitalOutput = gpioController.provisionDigitalOutputPin(raspiPin, PinState.HIGH);
        this.digitalOutput.setShutdownOptions(true, PinState.HIGH);
    }

    public void pressFor(final int millis) {
        try {
            digitalOutput.low();
            TimeUnit.MILLISECONDS.sleep(millis);
            digitalOutput.high();
        } catch (final InterruptedException ex) {
            logger.severe("Interrupt exception while waiting for digital output.");
        }
    }
}
