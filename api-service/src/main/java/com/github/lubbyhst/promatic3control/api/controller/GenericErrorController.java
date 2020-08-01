package com.github.lubbyhst.promatic3control.api.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenericErrorController implements ErrorController {

    private final static String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ResponseEntity notFound(){
        return new ResponseEntity<String>("unsupported request", HttpStatus.BAD_REQUEST);
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
