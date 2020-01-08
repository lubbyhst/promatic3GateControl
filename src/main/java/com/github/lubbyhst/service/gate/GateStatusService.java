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

@Service
public class GateStatusService {

    private static final Logger logger = Logger.getLogger(GateStatusService.class.getName());

    private final DigitalInput gateOpenPin;
    private final DigitalInput gateClosePin;
    private final DigitalInput gateVentilationPin;
    private final DigitalInput gateMovingPin;

    public GateStatusService(@Autowired final GatePinConfiguration gatePinConfiguration){
        gateOpenPin = new DigitalInput(gatePinConfiguration.getStatusPinOpen());
        gateClosePin = new DigitalInput(gatePinConfiguration.getStatusPinClose());
        gateVentilationPin = new DigitalInput(gatePinConfiguration.getStatusPinVentilation());
        gateMovingPin = new DigitalInput(gatePinConfiguration.getStatusPinMoving());
    }

    public GateStatus getActualGateStatus(){
        if(gateOpenPin.isHigh()){
            return GateStatus.OPEN;
        }
        if(gateClosePin.isHigh()){
            return GateStatus.CLOSED;
        }
        if (gateVentilationPin.isHigh()) {
            return GateStatus.VENTILATION;
        }
        //Assume partial open gate
        return GateStatus.OPEN;
    }

    private boolean isGateMoving(){
        return gateMovingPin.isHigh();
    }

    public void waitForGate(){
        int counter = 0;
        while (isGateMoving() && counter <= 30){
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
                            if(gateClosePin.isHigh() && GateStatus.CLOSED.equals(gateStatusToWaitFor)){
                                return GateStatus.CLOSED;
                            }
                            if(gateOpenPin.isHigh() && GateStatus.OPEN.equals(gateStatusToWaitFor)){
                                return GateStatus.OPEN;
                            }
                            if(gateVentilationPin.isHigh() && GateStatus.VENTILATION.equals(gateStatusToWaitFor)){
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

}
