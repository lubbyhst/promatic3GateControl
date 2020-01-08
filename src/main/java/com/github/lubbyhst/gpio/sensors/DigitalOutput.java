package com.github.lubbyhst.gpio.sensors;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class DigitalOutput {
    private static final Logger logger = Logger.getLogger(DigitalOutput.class.getName());

    private final Pin raspiPin;

    private final GpioPinDigitalOutput digitalOutput;

    public DigitalOutput(int pinAddress) {
        this.raspiPin = RaspiPin.getPinByAddress(pinAddress);
        this.digitalOutput = GpioFactory.getInstance().provisionDigitalOutputPin(raspiPin, PinState.LOW);
        this.digitalOutput.setShutdownOptions(true, PinState.LOW);
    }

    public void pressFor(int millis){
        try {
            digitalOutput.high();
            TimeUnit.MILLISECONDS.sleep(millis);
            digitalOutput.low();
        }catch (InterruptedException ex){
            logger.severe("Interrupt exception while waiting for digital output.");
        }
    }
}
