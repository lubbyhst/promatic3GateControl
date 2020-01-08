package com.github.lubbyhst.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

@Service
public class GpioService{
    private static final Logger logger = Logger.getLogger(GpioService.class.getName());

    public GpioService(@Value("${pi4j.platform}")
    final String platform) throws PlatformAlreadyAssignedException {
        PlatformManager.setPlatform(Platform.valueOf(platform));
    }

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("Init GpioService");
    }
}
