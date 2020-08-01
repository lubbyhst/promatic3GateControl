package com.github.lubbyhst.promatic3control.api.components;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.github.lubbyhst.promatic3control.api.enums.GateStatus;
import com.github.lubbyhst.promatic3control.api.service.DHT22Service;
import com.github.lubbyhst.promatic3control.api.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.lubbyhst.promatic3control.api.service.gate.GateControlService;
import com.github.lubbyhst.promatic3control.api.service.gate.GateStatusService;
import com.github.lubbyhst.promatic3control.api.service.gate.GateVentilationService;

@Component
@EnableAsync
public class JobScheduler {

    private static final Logger logger = Logger.getLogger(JobScheduler.class.getName());

    private final boolean gateSendMailOnStatusOpenJobEnabled;
    private final String mailRecipient;

    private boolean gateVentilationJobEnabled;
    private Instant gateOpenTimestamp;

    @Autowired
    private GateVentilationService gateVentilationService;

    @Autowired
    private GateStatusService gateStatusService;

    @Autowired
    private GateControlService gateControlService;

    @Autowired
    private DHT22Service dht22Service;

    @Autowired
    private MailService mailService;

    public JobScheduler(
            @Value("${gate.ventilation.job.enabled}")
            final boolean ventilationJobEnabled,
            @Value("${gate.send.mail.on.status.open.job.enabled}")
            final boolean gateSendMailOnStatusOpenJobEnabled,
            @Value("${gate.send.mail.on.status.open.recipient}")
            final String mailRecipient) {
        this.gateVentilationJobEnabled = ventilationJobEnabled;
        this.gateSendMailOnStatusOpenJobEnabled = gateSendMailOnStatusOpenJobEnabled;
        this.mailRecipient = mailRecipient;
    }

    @Scheduled(cron = "${gate.close.at.night.job.cron.expression}")
    @Async
    public void triggerCloseGateAtNight() {
        logger.info("Trigger close gate from ventilation status @night");
        if (GateStatus.VENTILATION.equals(gateStatusService.getActualGateStatus())) {
            gateControlService.closeGate();
        }
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

    @Scheduled(cron = "${gate.send.mail.on.status.open.job.cron.expression}")
    @Async
    public void triggerSendMailIfGateOpenCheck() {
        if (gateSendMailOnStatusOpenJobEnabled) {
            if (GateStatus.OPEN.equals(gateStatusService.getActualGateStatus())) {
                logger.info("Gate is open. Check if send mail is needed.");
                if (gateOpenTimestamp == null) {
                    logger.info("No timestamp found. Set new timestamp.");
                    gateOpenTimestamp = Instant.now();
                } else {
                    if (Instant.now().minus(Duration.ofHours(1)).isAfter(gateOpenTimestamp)) {
                        logger.info("Trigger gate send mail on status open job.");
                        final SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setTo(mailRecipient);
                        mailMessage.setSubject("GATE OPEN!");
                        mailMessage.setText("Gate open since " + gateOpenTimestamp);
                        mailService.sendMail(mailMessage);
                    }
                }
            } else {
                logger.info("Gate is not open reset timestamp.");
                gateOpenTimestamp = null;
            }
        }
    }

    public boolean isGateVentilationJobEnabled() {
        return gateVentilationJobEnabled;
    }

    public void setGateVentilationJobEnabled(final boolean gateVentilationJobEnabled) {
        this.gateVentilationJobEnabled = gateVentilationJobEnabled;
    }
}
