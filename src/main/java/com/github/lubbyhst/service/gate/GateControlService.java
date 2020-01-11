package com.github.lubbyhst.service.gate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.components.GatePinConfiguration;
import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.gpio.sensors.DigitalOutput;
import com.github.lubbyhst.service.GpioService;

@Service
public class GateControlService {

    private static final int defaultButtonPressTimeout = 250;
    private final GateStatusService gateStatusService;
    private final DigitalOutput gateVentilationButton;
    private final DigitalOutput gateCloseOpenButton;

    public GateControlService(@Autowired final GateStatusService gateStatusService,
            @Autowired
            final GatePinConfiguration gatePinConfiguration,
            @Autowired
            final GpioService gpioService) {
        this.gateStatusService = gateStatusService;
        this.gateVentilationButton = gpioService.getDigitalOutput(gatePinConfiguration.getSwitchPinVentilation());
        this.gateCloseOpenButton = gpioService.getDigitalOutput(gatePinConfiguration.getSwitchPinCloseOpen());
    }

    public GateStatus changeGateToVentilation(){
        if (gateStatusService.isGateInteractionInProgress()) {
            return gateStatusService.getActualGateStatus();
        }
        gateStatusService.setGateInteractionInProgress(true);
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        gateStatusService.setGateInteractionInProgress(false);
        return gateStatusService.getActualGateStatus();
    }

    public GateStatus closeGate(){
        if (gateStatusService.isGateInteractionInProgress()) {
            return gateStatusService.getActualGateStatus();
        }
        gateStatusService.setGateInteractionInProgress(true);
        if (GateStatus.CLOSED.equals(gateStatusService.getActualGateStatus())) {
            gateStatusService.setGateInteractionInProgress(false);
            return gateStatusService.getActualGateStatus();
        }
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        gateVentilationButton.pressFor(defaultButtonPressTimeout);
        gateStatusService.waitForGateStatus(GateStatus.CLOSED);
        gateStatusService.setGateInteractionInProgress(false);
        return gateStatusService.getActualGateStatus();
    }

    private GateStatus openGate() {
        if (gateStatusService.isGateInteractionInProgress()) {
            return gateStatusService.getActualGateStatus();
        }
        gateStatusService.setGateInteractionInProgress(true);
        if (GateStatus.OPEN.equals(gateStatusService.getActualGateStatus())) {
            gateStatusService.setGateInteractionInProgress(false);
            return gateStatusService.getActualGateStatus();
        }
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        //deactivated to avoid unwanted door opening
        //gateCloseOpenButton.pressFor(defaultButtonPressTimeout);
        gateStatusService.waitForGateStatus(GateStatus.OPEN);
        gateStatusService.setGateInteractionInProgress(false);
        return gateStatusService.getActualGateStatus();
    }

}
