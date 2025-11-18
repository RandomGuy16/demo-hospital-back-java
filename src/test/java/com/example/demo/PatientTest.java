package com.example.demo;

// to begin the test, there are a few things to import
// first, this annotation to run a function as a test with your build tool
import com.example.demo.models.Patient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired; // autowired: wires the object that simulates the API calls
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest; // this defines a class as a test and executes its tests functions
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc; // the object that simulates the API calls
import org.springframework.test.web.servlet.MvcResult;  // in testGetAllUsers, to pass the response as an argument, this is the necessary import

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // class with static API results functions (status, isOk, jsonPath, ...)


@SpringBootTest
@AutoConfigureMockMvc
public class PatientTest {
    private static final Logger logger = LoggerFactory.getLogger(PatientTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePatient() throws Exception {
        logger.info("Starting testCreatePatient");
        Patient mockPatient = new Patient(
                "Lamine",
                "Yamal",
                LocalDate.of(2006, 8, 20),
                "masculine",
                "948232081",
                "idk whats contacs",
                "blablabla",
                "Trapiche 777"
        );
        String json = objectMapper.writeValueAsString(mockPatient);
        // logger.debug("Created mock patient: {}", mockPatient);
        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(result ->
                    logger.info("POST /api/v1/patients Response: {}", result.getResponse().getContentAsString())
                )
                .andExpect(status().isCreated());


        // test with a second patient

        Patient satoruGojo = new Patient(
            "Satoru",
            "Gojo",
            LocalDate.of(1992, 12, 10),
            "masculine",
            "+1 948232081",
            "everyone",
            "ShokoIeiri-123456",
            "Habich123"
        );
        json = objectMapper.writeValueAsString(satoruGojo);
        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(result ->
                    logger.info("POST /api/v1/patients Response: {}", result.getResponse().getContentAsString())
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllPatients() throws Exception {
        logger.info("Starting testGetAllPatients");
        mockMvc.perform(get("/api/v1/patients"))
                .andDo(this::verityPatientsResponse)
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$").value(""));
    }

    private void verityPatientsResponse(MvcResult result) throws Exception {
        logger.info("GET /api/v1/patients Response status: {}", result.getResponse().getStatus());
        List<Patient> patients = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<List<Patient>>() {}
        );
        logger.info("Patients array length: {}", patients.size());
        logger.info("Patients: {}", patients);
        Assertions.assertEquals(2, patients.size());
    }

    @Test
    public void testRemoveNonExistingPatient() throws Exception {
        logger.info("Starting testRemoveNonExistingPatient");
        mockMvc.perform(delete(String.format("/api/v1/patients/%s", UUID.randomUUID())))
                .andDo(result ->
                    logger.info("DELETE URI: {}", String.format("/api/v1/patients/%s", UUID.randomUUID()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveExistingPatient() throws Exception {
        logger.info("Starting testRemoveExistingPatient");
        // Create a valid patient (don't inline "new Patient()", gives an error)
        Patient patientToCreate = new Patient(
            "Test",
            "Patient",
            LocalDate.of(1990, 1, 1),
            "masculine",
            "123456789",
            "test@example.com",
            "MRN-TEST-001",
            "Test Address 123"
        );

        // perform POST and get the result
        MvcResult createResult = mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientToCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse the response to get the patient ID
        Patient createdPatient = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), Patient.class
        );
        UUID patientId = createdPatient.getPatientId();

        // perform DELETE with the correct ID
        mockMvc.perform(delete(String.format("/api/v1/patients/%s", patientId))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
