package com.example.demo;

import com.example.demo.dto.PatientPatchRequest;
import com.example.demo.dto.PatientRequest;
import com.example.demo.models.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientControllerTest extends ControllerTestSupport {

    @Test
    void createPatientReturnsCreatedResponse() throws Exception {
        PatientRequest request = new PatientRequest(
                "Jane",
                "Doe",
                "1234567890",
                LocalDate.of(1995, 4, 18),
                "female",
                "+1 555 0100",
                "jane.doe@example.com",
                "123 Main St");

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/patients/")))
                .andExpect(jsonPath("$.patientId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.mrn").value(containsString("1234567890")));
    }

    @Test
    void createPatientWithInvalidPayloadReturnsBadRequest() throws Exception {
        PatientRequest request = new PatientRequest(
                "",
                "Doe",
                "123",
                LocalDate.now().plusDays(1),
                "female",
                "123",
                "",
                "");

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getAllPatientsReturnsPaginatedSortedContent() throws Exception {
        savePatient(
            "Zoe",
            "Zulu",
            "1111111111",
            "female",
            LocalDate.of(1990, 1, 1)
        );
        savePatient("Ana", "Alpha", "2222222222", "female", LocalDate.of(1991, 2, 2));

        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Alpha"))
                .andExpect(jsonPath("$.content[1].lastName").value("Zulu"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getPatientByIdReturnsPatient() throws Exception {
        Patient patient = savePatient(
            "Johan",
            "Smith",
            "1234567891",
            "male",
            LocalDate.of(1889, 3, 11)
        );

        mockMvc.perform(get("/api/v1/patients/{id}", patient.getPatientId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patient.getPatientId().toString()))
                .andExpect(jsonPath("$.mrn").value(patient.getMrn()));
    }

    @Test
    void getPatientByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/patients/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientByMrnReturnsPatient() throws Exception {
        Patient patient = savePatient(
            "Maki",
            "Zen'in",
            "314159256",
            "female",
            LocalDate.of(2001, 4, 19));

        mockMvc.perform(get("/api/v1/patients/mrn/{mrn}", patient.getMrn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patient.getPatientId().toString()))
                .andExpect(jsonPath("$.mrn").value(patient.getMrn()));
    }

    @Test
    void updatePatientReturnsUpdatedPatient() throws Exception {
        Patient patient = savePatient(
            "Black",
            "Widow",
            "50112345667",
            "female",
            LocalDate.of(1993, 2, 28)
        );
        PatientRequest request = new PatientRequest(
                "Scarlett",
                "Johansson",
                "1234567893",
                LocalDate.of(1984, 11, 22),
                "female",
                "+1 555 9999",
                "janet.smith@example.com",
                "456 Oak Ave");

        mockMvc.perform(put("/api/v1/patients/{id}", patient.getPatientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.mrn").value(patient.getMrn()));
    }

    @Test
    void patchPatientReturnsPatchedPatient() throws Exception {
        Patient patient = savePatient(
            "Noritoshi",
            "Kamo",
            "666917846",
            "male",
            LocalDate.of(1878, 11, 30)
        );

        PatientPatchRequest req = new PatientPatchRequest(
            "Kenjaku",
            "---",
            null,
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(patch("/api/v1/patients/{id}", patient.getPatientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Kenjaku"))
            .andExpect(jsonPath("$.lastName").value("---"))
            .andExpect(jsonPath("$.mrn").value(patient.getMrn()));
    }

    @Test
    void deletePatientReturnsDeletedPayload() throws Exception {
        Patient patient = savePatient(
            "Delete",
            "Me",
            "1234567894",
            "therian",
            LocalDate.of(1995, 5, 6)
        );

        mockMvc.perform(delete("/api/v1/patients/{id}", patient.getPatientId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patient.getPatientId().toString()))
                .andExpect(jsonPath("$.lastName").value("Me"));
    }

    @Test
    void getAllPatientsWithInvalidSizeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
