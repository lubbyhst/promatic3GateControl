package com.github.lubbyhst.components;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.lubbyhst.service.DHT22Service;
import com.github.lubbyhst.service.gate.GateVentilationService;

@Component
@EnableAsync
public class JobScheduler {

    private static final Logger logger = Logger.getLogger(JobScheduler.class.getName());
    private boolean gateVentilationJobEnabled;

    @Autowired
    private GateVentilationService gateVentilationService;

    @Autowired
    private DHT22Service dht22Service;

    public JobScheduler(
            @Value("${gate.ventilation.job.enabled}")
            final boolean ventilationJobEnabled) {
        this.gateVentilationJobEnabled = ventilationJobEnabled;
    }

    @Scheduled(cron = "${gate.sensor.service.read.data.cron.expression}")
    @Async
    public void triggerSensorRead() {
        logger.fine("Reading data from DHT22 sensors.");
        dht22Service.readSensorData();
        logger.fine("Finished reading data from DHT22 sensors.");
    }

    @Scheduled(cron = "${gate.ventilation.job.cron.expression}")
    @Async
    public void triggerVentilationCheck() {
        if (gateVentilationJobEnabled) {
            logger.info("Trigger ventilation check.");
            gateVentilationService.checkVentilationNeeded();
            return;
        }
        logger.fine("Skipping ventilation trigger.");
    }

    public boolean isGateVentilationJobEnabled() {
        return gateVentilationJobEnabled;
    }

    public void setGateVentilationJobEnabled(final boolean gateVentilationJobEnabled) {
        this.gateVentilationJobEnabled = gateVentilationJobEnabled;
    }
}
