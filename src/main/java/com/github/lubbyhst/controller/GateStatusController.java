package com.github.lubbyhst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.lubbyhst.service.BME280Service;
import com.github.lubbyhst.service.DHT22Service;
import com.github.lubbyhst.service.gate.GateControlService;
import com.github.lubbyhst.service.gate.GateStatusService;
import com.github.lubbyhst.service.gate.GateVentilationService;

@Controller
public class GateStatusController {

    @Autowired
    private GateVentilationService gateVentilationService;

    @Autowired
    private GateStatusService gateStatusService;

    @Autowired
    private GateControlService gateControlService;

    @Autowired
    private DHT22Service dht22Service;

    @Autowired
    private BME280Service bme280Service;

    private void setModelAttributes(final Model model) {
        model.addAttribute("dht22Result", dht22Service.getDataFromIndoorSensor());
        model.addAttribute("outdoorResults", bme280Service.getDataFromOutdoorSensor());
        model.addAttribute("gateStatus",
                gateStatusService.isGateInteractionInProgress() ? "moving" : this.gateStatusService.getActualGateStatus());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(final Model model) {
        setModelAttributes(model);
        return "dashboard";
    }

    @RequestMapping(value = "/check/ventilation", method = RequestMethod.GET)
    public String checkVentialtion(final Model model) {
        gateVentilationService.checkVentilationNeeded();
        setModelAttributes(model);
        return "dashboard";
    }

    @RequestMapping(value = "/gate/close", method = RequestMethod.GET)
    public String close(final Model model) {
        gateVentilationService.reset();
        gateControlService.closeGate();
        setModelAttributes(model);
        return "dashboard";
    }

    @RequestMapping(value = "/gate/ventilation", method = RequestMethod.GET)
    public String ventialtion(final Model model) {
        gateVentilationService.reset();
        gateControlService.changeGateToVentilation();
        setModelAttributes(model);
        return "dashboard";
    }

    @RequestMapping(value = "/gate/open", method = RequestMethod.GET)
    public String open(final Model model) {
        gateVentilationService.reset();
        gateControlService.openGate();
        setModelAttributes(model);
        return "dashboard";
    }
}
