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
    private static final float minimumHumidityDifference = 3;
    private static final int delayInMinutesAfterVentilationStarted = 30;
    private static final int delayInMinutesAfterVentilationStopped = 120;

    private static Instant ventilationStarted;
    private static Instant ventilationStopped;

    @Autowired
    private DHT22Service dht22Service;

    @Autowired
    private GateControlService gateControlService;

    @Autowired
    private GateStatusService gateStatusService;

    public void reset() {
        ventilationStarted = null;
        ventilationStopped = null;
    }

    public void checkVentilationNeeded() {
        if (gateStatusService.isGateInteractionInProgress()) {
            logger.info("Gate is currently in use. Skipping check until next iteration.");
            return;
        }
        final GateStatus gateStatus = gateStatusService.getActualGateStatus();
        logger.info(String.format("Start check if ventilation is needed. Gate status is: %s", gateStatus));
        if (ventilationStarted != null
                && Duration.between(ventilationStarted, Instant.now()).toMinutes() <= delayInMinutesAfterVentilationStarted) {
            logger.info("Ventilation started less than 30 minutes ago. Skipping ventilation check.");
            return;
        }
        if (ventilationStopped != null
                && Duration.between(ventilationStopped, Instant.now()).toMinutes() <= delayInMinutesAfterVentilationStopped) {
            logger.info(
                    "Ventilation stopped less than " + delayInMinutesAfterVentilationStopped + " minutes ago. Skipping ventilation check.");
            return;
        }
        if (GateStatus.OPEN.equals(gateStatus)) {
            logger.info("Gate actually open. Ventilation not needed.");
            return;
        }
        final DHT22Result indoor = dht22Service.getDataFromIndoorSensor();
        logger.info(String.format("Data from indoor sensor is humidity %s and temperature %s", indoor.getHumidityRelative(),
                indoor.getTemperature()));
        final DHT22Result outdoor = dht22Service.getDataFromOutdoorSensor();
        logger.info(String.format("Data from outdoor sensor is humidity %s and temperature %s", outdoor.getHumidityRelative(),
                outdoor.getTemperature()));
        final double dewPointIndoor = indoor.getDewPoint();
        logger.info(String.format("dew point indoor is %s C", dewPointIndoor));
        final double dewPointOutdoor = outdoor.getDewPoint();
        logger.info(String.format("dew point outdoor is %s C", dewPointOutdoor));
        if (dewPointOutdoor > dewPointIndoor) {
            logger.info(
                    String.format("Outdoor dew point (%s)C is greater than indoor dew point (%s)C. Skipping Ventilation.", dewPointOutdoor,
                            dewPointIndoor));
            closeGateIfNeeded(gateStatus);
            return;
        }
        if (dewPointOutdoor < dewPointIndoor) {
            logger.info(
                    String.format("Outdoor dew point (%s)C is lower than indoor dew point (%s)C. Ventilation is possible.", dewPointOutdoor,
                            dewPointIndoor));

            if (checkHumidity(indoor, outdoor)) {
                if (GateStatus.CLOSED.equals(gateStatus)) {
                    logger.info("Gate is close. Ventilation could be started.");
                    final GateStatus newGateSatus = gateControlService.changeGateToVentilation();
                    ventilationStarted = Instant.now();
                    if (GateStatus.CLOSED.equals(newGateSatus) || GateStatus.OPEN.equals(newGateSatus)) {
                        logger.warning("Gate changed not to ventilation status.");
                        return;
                    }
                    logger.info(String.format("Gate successful changed to %s", newGateSatus));
                    return;
                }
                logger.info(String.format("Gate is not closed. Skipping Gate Ventilation. Status was %s", gateStatus));
                return;
            } else {
                logger.info(String.format("Humidity check was negative. Skipping ventilation.", indoor.getHumidityRelative()));
            }
        }
        // branch to execute the negative way eg. close gate if ventilation are in progress
        closeGateIfNeeded(gateStatus);
    }

    private void closeGateIfNeeded(final GateStatus gateStatus) {
        if (GateStatus.VENTILATION.equals(gateStatus)) {
            logger.info(String.format("Closing gate because no need of ventilation."));
            ventilationStopped = Instant.now();
            gateControlService.closeGate();
        }
    }

    private boolean checkHumidity(final DHT22Result indoor, final DHT22Result outdoor) {
        logger.info(String.format("Checking if indoor humidity (%s) is greater than humidity threshold (%s).", indoor.getHumidityRelative(),
                humidityThreshold));
        if (indoor.getHumidityRelative() > humidityThreshold) {
            logger.info(String.format("Indoor humidity (%s percent) is greater than %s percent. Ventilation possible.",
                    indoor.getHumidityRelative(),
                            humidityThreshold));
            final double absoluteOutdoorHumidity = outdoor.getHumidityAbsolute();
            logger.info(String.format("Calculated absolute outdoor humidity of %s g/m3", absoluteOutdoorHumidity));
            final double absoluteIndoorHumidity = indoor.getHumidityAbsolute();
            logger.info(String.format("Calculated absolute indoor humidity of %s g/m3", absoluteIndoorHumidity));
            if ((absoluteOutdoorHumidity < absoluteIndoorHumidity)
                    && (absoluteIndoorHumidity - absoluteOutdoorHumidity) > minimumHumidityDifference) {
                logger.info("Outdoor humidity is lower than indoor humidity. Ventilation possible.");
                return true;
            }
            logger.info("Outdoor humidity is greater than indoor humidity. Ventilation skipped.");
        }
        return false;
    }
}
