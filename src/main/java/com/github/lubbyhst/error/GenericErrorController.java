package com.github.lubbyhst.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

@Controller
public class GenericErrorController {


    @GetMapping("/error")
    public String defaultError(){
        return "error.html";
    }
}
