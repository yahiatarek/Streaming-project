package com.example.limits_service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.limits_service.Bean.Limits;
import com.example.limits_service.Config.Configuration;

@RestController
public class LimitsController {
    private final Configuration configuration;

    public LimitsController(Configuration configuration) {
        this.configuration = configuration;
    }

    @GetMapping("/limits")
    public Limits limitsRetrieval() {
        return new Limits(configuration.getMinimum(), configuration.getMaximum());
    }
}
