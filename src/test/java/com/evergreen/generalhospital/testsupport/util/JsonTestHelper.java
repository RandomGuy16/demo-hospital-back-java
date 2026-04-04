package com.evergreen.generalhospital.testsupport.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class JsonTestHelper {
    @Autowired
    ObjectMapper objectMapper;

    /**
     * Serializes an object to JSON for test requests.
     *
     * @param value object to serialize.
     * @return JSON representation of the object.
     * @throws JsonProcessingException if serialization fails.
     */
    public String json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
