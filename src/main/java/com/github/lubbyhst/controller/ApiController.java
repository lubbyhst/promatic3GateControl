package com.github.lubbyhst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.lubbyhst.dto.BME280Result;
import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.service.BME280Service;
import com.github.lubbyhst.service.DHT22Service;
import com.github.lubbyhst.service.gate.GateStatusService;

@RestController
public class ApiController {

    @Autowired
    private GateStatusService gateStatusService;

    @Autowired
    private DHT22Service dht22Service;

    @Autowired
    private BME280Service bme280Service;

    @RequestMapping(value = "/api", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String defaultReturn() {
        return "unsupported request";
    }

    @RequestMapping(value = "/api/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectNode getStatus() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode indoorNode = mapper.createObjectNode();
        indoorNode.put("humidityRelative", dht22Service.getDataFromIndoorSensor().getHumidityRelative());
        indoorNode.put("humidityAbsolute", dht22Service.getDataFromIndoorSensor().getHumidityAbsolute());
        indoorNode.put("temperature", dht22Service.getDataFromIndoorSensor().getTemperature());
        final ObjectNode outdoorNode = mapper.createObjectNode();
        outdoorNode.put("humidityRelative", dht22Service.getDataFromOutdoorSensor().getHumidityRelative());
        outdoorNode.put("humidityAbsolute", dht22Service.getDataFromOutdoorSensor().getHumidityAbsolute());
        outdoorNode.put("temperature", dht22Service.getDataFromOutdoorSensor().getTemperature());
        final GateStatus gateStatus = gateStatusService.getActualGateStatus();
        final ObjectNode gateNode = mapper.createObjectNode();
        gateNode.put("status", gateStatus.name());
        gateNode.put("statusLabel", gateStatus.getLabel());
        gateNode.put("numericStatus", gateStatus.getNumericStatus());
        final ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("gate", gateNode);
        rootNode.set("indoor", indoorNode);
        rootNode.set("outdoor", outdoorNode);
        return rootNode;
    }

    @RequestMapping(value = "/api/indoor/humidity", method = RequestMethod.GET)
    public String getIndoorHumidity() {
        return String.valueOf(dht22Service.getDataFromIndoorSensor().getHumidityRelative());
    }

    @RequestMapping(value = "/api/indoor/temperature", method = RequestMethod.GET)
    public String getIndoorTemerature() {
        return String.valueOf(dht22Service.getDataFromIndoorSensor().getTemperature());
    }

    @RequestMapping(value = "/api/outdoor/humidity", method = RequestMethod.GET)
    public String getOutdoorHumidity() {
        return String.valueOf(dht22Service.getDataFromIndoorSensor().getHumidityRelative());
    }

    @RequestMapping(value = "/api/outdoor/temperature", method = RequestMethod.GET)
    public String getOutdoorTemerature() {
        return String.valueOf(dht22Service.getDataFromIndoorSensor().getTemperature());
    }

    @RequestMapping(value = "/api/outdoor/bme280", method = RequestMethod.GET)
    public ResponseEntity<BME280Result> getBME280Result() {
        return ResponseEntity.ok(bme280Service.getDataFromOutdoorSensor());
    }
}
