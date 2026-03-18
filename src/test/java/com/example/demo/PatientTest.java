package com.example.demo;

// to begin the test, there are a few things to import
// first, this annotation to run a function as a test with your build tool
import com.example.demo.dto.PatientRequest;
import com.example.demo.models.Patient;
import com.example.demo.logging.TestLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MvcResult; // in testGetAllUsers, to pass the response as an argument, this is the necessary import
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // class with static API results functions (status, isOk, jsonPath, ...)

@SpringBootTest
@AutoConfigureMockMvc
public class PatientTest {
    private static final Logger logger = LoggerFactory.getLogger(PatientTest.class);
    private static final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePatient() throws Exception {
        logger.info("Starting testCreatePatient");
        PatientRequest mockPatient = new PatientRequest(
                "Lamine",
                "Yamal",
                "6666666666",
                LocalDate.of(2006, 8, 20),
                "masculine",
                "948232081",
                "idk whats contacs",
                "Trapiche 777");
        String json = objectMapper.writeValueAsString(mockPatient);
        // logger.debug("Created mock patient: {}", mockPatient);
        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(result -> TestLog.response(logger, "POST /api/v1/patients", result))
                .andExpect(status().isCreated());

        // test with a second patient
        PatientRequest satoruGojo = new PatientRequest(
                "Satoru",
                "Gojo",
                "71962299",
                LocalDate.of(1992, 12, 10),
                "masculine",
                "+1 948232081",
                "everyone",
                "Habich123");
        json = objectMapper.writeValueAsString(satoruGojo);
        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(result -> TestLog.response(logger, "POST /api/v1/patients", result))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetNonExistingPatient() throws Exception {
        logger.info("Starting testGetNonExistingPatient");
        UUID randomId = UUID.randomUUID();
        URI location = uriBuilder.path("/api/v1/patients/{id}").buildAndExpand(randomId).toUri();
        logger.info("Location: {}", location);
        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllPatients() throws Exception {
        logger.info("Starting testGetAllPatients");
        mockMvc.perform(get("/api/v1/patients"))
                .andDo(this::verifyPatientsResponse)
                .andExpect(status().isOk());
        // .andExpect(jsonPath("$").value(""));
    }

    private void verifyPatientsResponse(MvcResult result) throws Exception {
        logger.info("GET /api/v1/patients Response status: {}", result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());

        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = rootNode.get("content");
        List<Patient> patients = objectMapper.convertValue(contentNode, new TypeReference<>() {
        });

        logger.info("Patients array length: {}", patients.size());
        logger.info("Patients: {}", patients);
        Assertions.assertEquals(2, patients.size());
    }

    @Test
    public void testRemoveNonExistingPatient() throws Exception {
        logger.info("Starting testRemoveNonExistingPatient");
        mockMvc.perform(delete(String.format("/api/v1/patients/%s", UUID.randomUUID())))
                .andDo(result -> logger.info("DELETE URI: {}", String.format("/api/v1/patients/%s", UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveExistingPatient() throws Exception {
        logger.info("Starting testRemoveExistingPatient");
        // Create a valid patient (don't inline "new Patient()", gives an error)
        PatientRequest patientToCreate = new PatientRequest(
                "Test",
                "Patient",
                "123456789",
                LocalDate.of(1990, 1, 1),
                "masculine",
                "123456789",
                "test@example.com",
                "Test Address 123");

        // perform POST and get the result
        MvcResult createResult = mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientToCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse the response to get the patient ID
        Patient createdPatient = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Patient.class);
        UUID patientId = createdPatient.getPatientId();

        // perform DELETE with the correct ID
        mockMvc.perform(delete(String.format("/api/v1/patients/%s", patientId))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
