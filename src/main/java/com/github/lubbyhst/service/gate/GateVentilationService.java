package com.github.lubbyhst.service.gate;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lubbyhst.dto.DHT22Result;
import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.service.DHT22Service;

@Service
public class GateVentilationService {

    private static final Logger logger = Logger.getLogger(GateVentilationService.class.getName());

    private static final float humidityThreshold = 60;
    private static final int delayAfterVentilationStarted = 30;

    private static Instant ventilationStarted;

    @Autowired
    private DHT22Service dht22Service;

    @Autowired
    private GateControlService gateControlService;

    @Autowired
    private GateStatusService gateStatusService;

    public void checkVentilationNeeded() {
        gateStatusService.waitForGate();
        final GateStatus gateStatus = gateStatusService.getActualGateStatus();
        logger.info(String.format("Start check if ventilation is needed. Gate status is: %s", gateStatus));
        if (ventilationStarted != null && Duration.between(Instant.now(), ventilationStarted).toMinutes() <= delayAfterVentilationStarted) {
            logger.info("Ventilation started less than 30 minutes ago. Skipping ventilation check.");
            return;
        }
        if (GateStatus.OPEN.equals(gateStatus)) {
            logger.info("Gate actually open. Ventilation not needed.");
            return;
        }
        final DHT22Result indoor = dht22Service.getDataFromIndoorSensor();
        logger.info(
                String.format("Data from indoor sensor is humidity %s and temperature %s", indoor.getHumidity(), indoor.getTemperature()));
        final DHT22Result outdoor = dht22Service.getDataFromOutdoorSensor();
        logger.info(String.format("Data from outdoor sensor is humidity %s and temperature %s", outdoor.getHumidity(),
                outdoor.getTemperature()));
        final double dewPointIndoor = calculateDewPoint(indoor);
        logger.info(String.format("dew point indoor is %s C", dewPointIndoor));
        final double dewPointOutdoor = calculateDewPoint(outdoor);
        logger.info(String.format("dew point outdoor is %s C", dewPointOutdoor));
        if (dewPointOutdoor > dewPointIndoor) {
            logger.info(
                    String.format("Outdoor dew point (%s)C is greater than indoor dew point (%s)C. Skipping Ventilation.", dewPointOutdoor,
                            dewPointIndoor));
            return;
        }
        if (dewPointOutdoor < dewPointIndoor) {
            logger.info(
                    String.format("Outdoor dew point (%s)C is lower than indoor dew point (%s)C. Ventilation is possible.", dewPointOutdoor,
                            dewPointIndoor));

            if (indoor.getHumidity() > humidityThreshold) {
                logger.info(String.format("Indoor humidity (%s percent) is greater than 60 percent. Open gate to ventilation position.",
                        indoor.getHumidity()));
                if (GateStatus.CLOSED.equals(gateStatus)) {
                    logger.info("Gate is close. Ventilation could be started.");
                    final GateStatus newGateSatus = gateControlService.changeGateToVentilation();
                    ventilationStarted = Instant.now();
                    if (GateStatus.CLOSED.equals(newGateSatus) || GateStatus.OPEN.equals(newGateSatus)) {
                        logger.warning("Gate changed not to ventilation status.");
                        return;
                    }
                    logger.info(String.format("Gate changed to %s", newGateSatus));
                }
                logger.info(String.format("Gate is not closed. Skipping Gate Ventilation. Status was %s", gateStatus));
                return;
            } else {
                logger.info(String.format("Indoor humidity (%s percent) is lower than 60 percent. Skipping ventilation.",
                        indoor.getHumidity()));
                if (GateStatus.VENTILATION.equals(gateStatus)) {
                    logger.info(String.format("Closing gate because no need of ventilation."));
                    gateControlService.closeGate();
                }
            }
        }

    }

    private double calculateDewPoint(final DHT22Result dht22Result) {
        final float temperature = dht22Result.getTemperature();
        final float humidity = dht22Result.getHumidity();
        return 243.12 * ((17.62 * temperature) / (243.12 + temperature) + Math.log(humidity / 100)) / (
                (17.62 * 243.12) / (243.12 + temperature) - Math.log(humidity / 100));
    }
}
