package com.github.lubbyhst.controller;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseWebMvcController {

    private static final Logger logger = Logger.getLogger(SseWebMvcController.class.getName());
    private SseEmitter emitter;

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createConnection() {
        emitter = new SseEmitter();
        return emitter;
    }

    public void sendEvent(final SseEmitter.SseEventBuilder sseEventBuilder) {
        try {
            emitter.send(sseEventBuilder);
        } catch (final IOException e) {
            logger.warning("Error sending event to client.");
            emitter.completeWithError(e);
        }
    }
}
