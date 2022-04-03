package com.github.lubbyhst.service;

import java.time.Duration;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.lubbyhst.enums.GateStatus;
import com.github.lubbyhst.service.gate.GateStatusService;
import com.github.lubbyhst.service.gate.GateVentilationService;

@RunWith(PowerMockRunner.class)
public class GateVentilationServiceTest {

    @Mock
    private GateStatusService gateStatusService;

    @Mock
    private Instant ventilationStarted;

    @InjectMocks
    private GateVentilationService gateVentilationService;

    @Test
    public void testTimeBetweenChecksWithinTimeout() {
        Mockito.when(gateStatusService.getActualGateStatus()).thenReturn(GateStatus.CLOSED);
        Mockito.when(gateStatusService.isGateInteractionInProgress()).thenReturn(false);
        //gateVentilationService.checkVentilationNeeded();
    }

    @Test
    public void testTimeBetweenChecksTimeoutExceeded() {
        final long time = Duration.between(Instant.now().minusSeconds(1800), Instant.now().minusSeconds(0)).toMinutes();
        Assert.assertEquals(30, time);
    }

}
