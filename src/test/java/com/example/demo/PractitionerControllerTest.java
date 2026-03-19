package com.example.demo;

import com.example.demo.dto.PractitionerRequest;
import com.example.demo.models.Practitioner;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PractitionerControllerTest extends ControllerTestSupport {

    @Test
    void createPractitionerReturnsCreatedResponse() throws Exception {
        PractitionerRequest request = new PractitionerRequest(
                "Gregory",
                "House",
                "1234567890",
                LocalDate.of(1970, 6, 11),
                "male",
                "+1 555 0200",
                "house@example.com",
                List.of("Diagnostics", "Nephrology"));

        mockMvc.perform(post("/api/v1/practitioners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.practitionerId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Gregory"))
                .andExpect(jsonPath("$.specialties", contains("Diagnostics", "Nephrology")));
    }

    @Test
    void createPractitionerWithInvalidPayloadReturnsBadRequest() throws Exception {
        PractitionerRequest request = new PractitionerRequest(
                "",
                "House",
                "123",
                LocalDate.now().plusDays(1),
                "male",
                "123",
                "",
                List.of());

        mockMvc.perform(post("/api/v1/practitioners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getAllPractitionersReturnsSortedPage() throws Exception {
        savePractitioner("Meredith", "Grey", "1234567891", List.of("Surgery"));
        savePractitioner("Stephen", "Strange", "1234567892", List.of("Neurology"));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("sort", "lastName,asc")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Grey"))
                .andExpect(jsonPath("$.content[1].lastName").value("Strange"));
    }

    @Test
    void getPractitionerByIdReturnsPractitioner() throws Exception {
        Practitioner practitioner = savePractitioner("Stephen", "Strange", "1234567893", List.of("Neurology"));

        mockMvc.perform(get("/api/v1/practitioners/{id}", practitioner.getPractitionerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.practitionerId").value(practitioner.getPractitionerId().toString()))
                .andExpect(jsonPath("$.specialties[0]").value("Neurology"));
    }

    @Test
    void updatePractitionerReturnsUpdatedPractitioner() throws Exception {
        Practitioner practitioner = savePractitioner("Gregory", "House", "1234567894", List.of("Diagnostics"));
        PractitionerRequest request = new PractitionerRequest(
                "James",
                "Wilson",
                "1234567894",
                LocalDate.of(1975, 7, 1),
                "male",
                "+1 555 3333",
                "wilson@example.com",
                List.of("Oncology"));

        mockMvc.perform(put("/api/v1/practitioners/{id}", practitioner.getPractitionerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.lastName").value("Wilson"))
                .andExpect(jsonPath("$.specialties[0]").value("Oncology"));
    }

    @Test
    void deletePractitionerReturnsDeletedPayload() throws Exception {
        Practitioner practitioner = savePractitioner("Delete", "Doctor", "1234567895", List.of("General"));

        mockMvc.perform(delete("/api/v1/practitioners/{id}", practitioner.getPractitionerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.practitionerId").value(practitioner.getPractitionerId().toString()))
                .andExpect(jsonPath("$.lastName").value("Doctor"));
    }

    @Test
    void getPractitionerByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/practitioners/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
