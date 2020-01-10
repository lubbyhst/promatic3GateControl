package com.github.lubbyhst.service.gate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.components.GatePinConfiguration;
import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.gpio.sensors.DigitalInput;
import com.github.lubbyhst.service.GpioService;

@Service
public class GateStatusService {

    private static final Logger logger = Logger.getLogger(GateStatusService.class.getName());

    private final DigitalInput gateOpenPin;
    private final DigitalInput gateClosePin;
    private final DigitalInput gateVentilationPin;
    private final DigitalInput gateMovingPin;

    private boolean gateInteractionInProgress = false;

    public GateStatusService(
            @Autowired
            final GatePinConfiguration gatePinConfiguration,
            @Autowired
            final GpioService gpioService) {
        gateOpenPin = gpioService.getDigitalInput(gatePinConfiguration.getStatusPinOpen());
        gateClosePin = gpioService.getDigitalInput(gatePinConfiguration.getStatusPinClose());
        gateVentilationPin = gpioService.getDigitalInput(gatePinConfiguration.getStatusPinVentilation());
        gateMovingPin = gpioService.getDigitalInput(gatePinConfiguration.getStatusPinMoving());
    }

    public GateStatus getActualGateStatus(){
        if (gateOpenPin.isLow()) {
            return GateStatus.OPEN;
        }
        if (gateClosePin.isLow()) {
            return GateStatus.CLOSED;
        }
        if (gateVentilationPin.isLow()) {
            return GateStatus.VENTILATION;
        }
        //Assume partial open gate
        return GateStatus.OPEN;
    }

    public boolean isGateInteractionInProgress() {
        return gateInteractionInProgress;//no moving sensor installed
        //return gateMovingPin.isLow();
    }

    public void waitForGate(){
        int counter = 0;
        while (isGateInteractionInProgress() && counter <= 60) {
            try {
                logger.info("Gate is moving. Waiting until gate stops.");
                TimeUnit.SECONDS.sleep(1);
            } catch (final InterruptedException e) {
                logger.severe("Interrupt exception while waiting for gate. " + e.getMessage());
            }
            counter++;
        }
    }

    public GateStatus waitForGateStatus(final GateStatus gateStatusToWaitFor){
        GateStatus gateStatus = getActualGateStatus();
        try {
             gateStatus = new FluentWait<>(gateStatusToWaitFor)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofSeconds(1)).until(new Function<GateStatus, GateStatus>() {
                        @Override
                        public GateStatus apply(final GateStatus gateStatusToWaitFor) {
                            if (getActualGateStatus().equals(gateStatusToWaitFor) && GateStatus.CLOSED.equals(gateStatusToWaitFor)) {
                                return GateStatus.CLOSED;
                            }
                            if (getActualGateStatus().equals(gateStatusToWaitFor) && GateStatus.OPEN.equals(gateStatusToWaitFor)) {
                                return GateStatus.OPEN;
                            }
                            if (getActualGateStatus().equals(gateStatusToWaitFor) && GateStatus.VENTILATION.equals(gateStatusToWaitFor)) {
                                return GateStatus.VENTILATION;
                            }
                            logger.info(String.format("Waiting for gate to change status from %s to %s", getActualGateStatus(), gateStatusToWaitFor));
                            return null;
                        }
                    });
        }catch (final TimeoutException ex){
            logger.severe(String.format("Gate did not reach the expected status within timeout. Actual status %s, Status wanted %s", gateStatus, gateStatusToWaitFor));
        }
        return gateStatus;
    }

    public void setGateInteractionInProgress(final boolean interactionInProgress) {
        this.gateInteractionInProgress = interactionInProgress;
    }

}
