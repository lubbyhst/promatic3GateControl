package com.github.lubbyhst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.lubbyhst.service.DHT22Service;
import com.github.lubbyhst.service.GpioService;
import com.github.lubbyhst.service.gate.GateControlService;
import com.github.lubbyhst.service.gate.GateStatusService;
import com.github.lubbyhst.service.gate.GateVentilationService;

@Controller
public class TestController {

    @Autowired
    private GpioService gpioService;

    @Autowired
    private GateVentilationService gateVentilationService;

    @Autowired
    private GateStatusService gateStatusService;

    @Autowired
    private GateControlService gateControlService;

    @Autowired
    private DHT22Service dht22Service;

    @GetMapping("/")
    public String main(final Model model){
        model.addAttribute("message", "User!");
        model.addAttribute("dht22Result", dht22Service.getDataFromIndoorSensor());
        model.addAttribute("gateStatus", this.gateStatusService.getActualGateStatus());

        return "dashboard";
    }

    @PostMapping("/")
    public String execute(final Model model){
        gateVentilationService.checkVentilationNeeded();
        model.addAttribute("message", "User!");
        model.addAttribute("dht22Result", dht22Service.getDataFromIndoorSensor());
        model.addAttribute("gateStatus", this.gateStatusService.getActualGateStatus());
        return "dashboard";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = "action=close")
    public void save() {
        gateControlService.closeGate();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = "action=ventilation")
    public void ventialtion() {
        gateControlService.changeGateToVentilation();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = "action=open")
    public void open() {
        gateControlService.openGate();
    }
}
