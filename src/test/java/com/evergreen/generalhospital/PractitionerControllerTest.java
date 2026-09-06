package com.evergreen.generalhospital;

import com.evergreen.generalhospital.dto.practitioner.PractitionerRequest;
import com.evergreen.generalhospital.errors.ErrorCode;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.testsupport.base.CrudControllerTestSupport;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

class PractitionerControllerTest extends CrudControllerTestSupport {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void createPractitionerReturnsCreatedResponse() throws Exception {
        cleanDatabase();
        domainFixtures.seedDefaultSubjects();
        domainFixtures.seedFunnySubjects();
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
    void createPractitionerWithExistingIdNumberReturnsConflict() throws Exception {
        // yes I grabbed the previous test subject 'cuz I'm lazy
        // save the practitioner
        savePractitioner("Gregory", "House", "1234567890", List.of("Diagnostics", "Nephrology"));

        // now try to create a practitioner with the same id number
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
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ErrorCode.CONFLICT.name()));
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
        cleanDatabase();
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
    void updatePractitionerWithChangedIdNumberReturnsConflict() throws Exception {
        Practitioner practitioner = savePractitioner("Gregory", "House", "1234567894", List.of("Diagnostics"));
        PractitionerRequest request = new PractitionerRequest(
                "James",
                "Wilson",
                "1234567890",
                LocalDate.of(1975, 7, 1),
                "male",
                "+1 555 3333",
                "wilson@example.com",
                List.of("Oncology"));

        mockMvc.perform(put("/api/v1/practitioners/{id}", practitioner.getPractitionerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void deletePractitionerReturnsDeletedPayload() throws Exception {
        Practitioner practitioner = savePractitioner("Delete", "Doctor", "1234567895", List.of("General"));

        mockMvc.perform(delete("/api/v1/practitioners/{id}", practitioner.getPractitionerId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPractitionerByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/practitioners/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPractitionersWithPaginationAndSortingReturnsPagedResults() throws Exception {
        cleanDatabase();
        savePractitioner("Doctor", "Alpha", "1000000001", List.of("General"));
        savePractitioner("Doctor", "Beta", "1000000002", List.of("General"));
        savePractitioner("Doctor", "Gamma", "1000000003", List.of("General"));
        savePractitioner("Doctor", "Delta", "1000000004", List.of("General"));
        savePractitioner("Doctor", "Epsilon", "1000000005", List.of("General"));

        // Page 0, size 2, sorted by lastName asc (Alpha, Beta, Delta, Epsilon, Gamma)
        mockMvc.perform(get("/api/v1/practitioners")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Alpha"))
                .andExpect(jsonPath("$.content[1].lastName").value("Beta"))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        // Page 1, size 2
        mockMvc.perform(get("/api/v1/practitioners")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Delta"))
                .andExpect(jsonPath("$.content[1].lastName").value("Epsilon"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));

        // Page 2, size 2
        mockMvc.perform(get("/api/v1/practitioners")
                        .param("page", "2")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("Gamma"))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getPractitionersFilteredByDepartmentReturnsPractitionersInDepartment() throws Exception {
        cleanDatabase();
        Department cardio = saveDepartment("Cardiology", "Heart department");
        Department neuro = saveDepartment("Neurology", "Brain department");

        Practitioner doc1 = savePractitioner("Meredith", "Grey", "1000000001", List.of("Surgery"));
        Practitioner doc2 = savePractitioner("Cristina", "Yang", "1000000002", List.of("Cardiothoracic"));
        Practitioner doc3 = savePractitioner("Derek", "Shepherd", "1000000003", List.of("Neurosurgery"));

        cardio.getPractitioners().addAll(List.of(doc1, doc2));
        departmentRepository.save(cardio);

        neuro.getPractitioners().add(doc3);
        departmentRepository.save(neuro);

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Grey"))
                .andExpect(jsonPath("$.content[1].lastName").value("Yang"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getPractitionersFilteredByDepartmentWithPagination() throws Exception {
        cleanDatabase();
        Department cardio = saveDepartment("Cardiology", "Heart department");

        Practitioner doc1 = savePractitioner("Doc", "One", "1000000001", List.of("Cardiology"));
        Practitioner doc2 = savePractitioner("Doc", "Two", "1000000002", List.of("Cardiology"));
        Practitioner doc3 = savePractitioner("Doc", "Three", "1000000003", List.of("Cardiology"));

        cardio.getPractitioners().addAll(List.of(doc1, doc2, doc3));
        departmentRepository.save(cardio);

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getPractitionersFilteredByNonExistentDepartmentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.name()));
    }

    @Test
    void getPractitionersFilteredBySpecialtyReturnsMatchingPractitioners() throws Exception {
        cleanDatabase();
        savePractitioner("Doctor", "Cardio1", "1000000001", List.of("Cardiology"));
        savePractitioner("Doctor", "Neuro", "1000000002", List.of("Neurology"));
        savePractitioner("Doctor", "Cardio2", "1000000003", List.of("Cardiology", "Internal Medicine"));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("specialty", "Cardiology")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Cardio1"))
                .andExpect(jsonPath("$.content[1].lastName").value("Cardio2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getPractitionersFilteredBySpecialtyWithPagination() throws Exception {
        cleanDatabase();
        savePractitioner("Doctor", "One", "1000000001", List.of("Pediatrics"));
        savePractitioner("Doctor", "Two", "1000000002", List.of("Pediatrics"));
        savePractitioner("Doctor", "Three", "1000000003", List.of("Pediatrics"));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("specialty", "Pediatrics")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("specialty", "Pediatrics")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getPractitionersFilteredByDepartmentAndSpecialtyReturnsMatchingPractitioners() throws Exception {
        cleanDatabase();
        Department cardio = saveDepartment("Cardiology", "Heart department");
        Department emergency = saveDepartment("Emergency", "ER");

        Practitioner doc1 = savePractitioner("Doctor", "CardioInDept", "1000000001", List.of("Cardiology"));
        Practitioner doc2 = savePractitioner("Doctor", "GeneralInDept", "1000000002", List.of("General Practice"));
        Practitioner doc3 = savePractitioner("Doctor", "CardioInOtherDept", "1000000003", List.of("Cardiology"));

        cardio.getPractitioners().addAll(List.of(doc1, doc2));
        departmentRepository.save(cardio);

        emergency.getPractitioners().add(doc3);
        departmentRepository.save(emergency);

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("specialty", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("CardioInDept"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPractitionersFilteredByDepartmentAndSpecialtyWithPagination() throws Exception {
        cleanDatabase();
        Department cardio = saveDepartment("Cardiology", "Heart department");

        Practitioner doc1 = savePractitioner("Doctor", "One", "1000000001", List.of("Cardiology"));
        Practitioner doc2 = savePractitioner("Doctor", "Two", "1000000002", List.of("Cardiology"));
        Practitioner doc3 = savePractitioner("Doctor", "Three", "1000000003", List.of("Cardiology"));

        cardio.getPractitioners().addAll(List.of(doc1, doc2, doc3));
        departmentRepository.save(cardio);

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("specialty", "Cardiology")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("specialty", "Cardiology")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getPractitionersWithBlankSpecialtyFallsBackGracefully() throws Exception {
        cleanDatabase();
        savePractitioner("Doctor", "One", "1000000001", List.of("Cardiology"));
        savePractitioner("Doctor", "Two", "1000000002", List.of("Neurology"));

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("specialty", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void getPractitionersWithBlankSpecialtyAndDepartmentIdFallsBackToDepartment() throws Exception {
        cleanDatabase();
        Department cardio = saveDepartment("Cardiology", "Heart department");
        Practitioner doc1 = savePractitioner("Doctor", "CardioDoc", "1000000001", List.of("Cardiology"));
        savePractitioner("Doctor", "OtherDoc", "1000000002", List.of("Neurology"));

        cardio.getPractitioners().add(doc1);
        departmentRepository.save(cardio);

        mockMvc.perform(get("/api/v1/practitioners")
                        .param("departmentId", cardio.getDepartmentId().toString())
                        .param("specialty", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("CardioDoc"));
    }
}
