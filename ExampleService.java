package com.example.demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleService {
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);

    public void processRequest(String request) {
        logger.info("Processing request: {}", request);
        try {
            // Business logic here
        } catch (Exception e) {
            logger.error("Error processing request", e);
        }
    }
}

