package com.javaguru.contactservice.controller;


import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final static Logger LOGGER = Logger.getLogger(PropertyController.class.getName());

    @Autowired
    private Environment environment;

    @Value("${config-name}")
    String name = "Something";

    @GetMapping("/get")
    public ResponseEntity<?> propertyParameter() {
        LOGGER.log(Level.INFO, "INFO CONTACTS SERVICE");
        LOGGER.log(Level.SEVERE, "SEVERE CONTACTS SERVICE");
        System.out.println("parameter: " + name);
        return ResponseEntity.ok().body("Contacts service is running on port " + environment.getProperty("local.server.port"));
    }

    @Value("${config-name}")
    private String configName;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @GetMapping("/get2")
    public String getConfigName() {
        logger.info("Config name: {}", configName);
        return "Config name: " + configName;
    }
}
