package com.example.demo.logging;

import org.slf4j.Logger;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public final class TestLog {

    private TestLog() {}

    public static void start(Logger logger, String testName) {
        logger.info("===== Starting {} =====", testName);
    }

    public static void section(Logger logger, String sectionName) {
        logger.info("----- {} -----", sectionName);
    }

    public static void response(Logger logger, String label, MvcResult result) throws UnsupportedEncodingException {
        logger.info("{} response: {}", label, result.getResponse().getContentAsString());
    }
}
