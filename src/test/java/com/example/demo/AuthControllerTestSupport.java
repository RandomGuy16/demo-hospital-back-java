package com.example.demo;

import com.example.demo.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AuthControllerTestSupport extends TestSubjectsSeeder {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;


    protected String json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

}
