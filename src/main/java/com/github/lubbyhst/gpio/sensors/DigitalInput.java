package com.github.lubbyhst.gpio.sensors;

import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class DigitalInput {

    private static final Logger logger = Logger.getLogger(DigitalInput.class.getName());

    private final Pin raspiPin;

    private final GpioPinDigitalInput digitalInput;

    public DigitalInput(int pinAddress) {
        this.raspiPin = RaspiPin.getPinByAddress(pinAddress);
        this.digitalInput = GpioFactory.getInstance().provisionDigitalInputPin(raspiPin, PinPullResistance.PULL_DOWN);
        this.digitalInput.setShutdownOptions(true);
        this.digitalInput.addListener((GpioPinListenerDigital) gpioPinDigitalStateChangeEvent ->
                logger.info(String.format("Input pin (address=%s) changed state to %s",pinAddress, gpioPinDigitalStateChangeEvent.getState())));
    }

    public boolean isHigh(){
        return digitalInput.isHigh();
    }
}
