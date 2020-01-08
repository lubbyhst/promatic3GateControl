package com.github.lubbyhst.service.gate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.components.GatePinConfiguration;
import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.gpio.sensors.DigitalOutput;

@Service
public class GateControlService {

    private static final int defaultButtonPressTimeout = 250;
    private final GateStatusService gateStatusService;
    private final DigitalOutput gateVentilationButton;
    private final DigitalOutput gateCloseOpenButton;

    public GateControlService(@Autowired final GateStatusService gateStatusService,
            @Autowired final GatePinConfiguration gatePinConfiguration) {
        this.gateStatusService = gateStatusService;
        this.gateVentilationButton = new DigitalOutput(gatePinConfiguration.getSwitchPinVentilation());
        this.gateCloseOpenButton = new DigitalOutput(gatePinConfiguration.getSwitchPinCloseOpen());
    }

    public GateStatus changeGateToVentilation(){
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            return gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        return gateStatusService.getActualGateStatus();
    }

    public GateStatus closeGate(){
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        gateVentilationButton.pressFor(defaultButtonPressTimeout);
        return gateStatusService.waitForGateStatus(GateStatus.CLOSED);
    }

    private GateStatus openGate(){
        if(!GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())){
            gateVentilationButton.pressFor(defaultButtonPressTimeout);
            gateStatusService.waitForGateStatus(GateStatus.VENTILATION);
        }
        gateCloseOpenButton.pressFor(defaultButtonPressTimeout);
        return gateStatusService.waitForGateStatus(GateStatus.OPEN);
    }


}
