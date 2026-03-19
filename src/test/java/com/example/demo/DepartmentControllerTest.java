package com.example.demo;

import com.example.demo.dto.DepartmentRequest;
import com.example.demo.models.Department;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentControllerTest extends ControllerTestSupport {

    @Test
    void createDepartmentReturnsCreatedResponse() throws Exception {
        DepartmentRequest request = new DepartmentRequest("Cardiology", "Handles heart care");

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.departmentId").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.description").value("Handles heart care"));
    }

    @Test
    void createDepartmentWithInvalidPayloadReturnsBadRequest() throws Exception {
        DepartmentRequest request = new DepartmentRequest("", "");

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getAllDepartmentsReturnsSortedPage() throws Exception {
        saveDepartment("Radiology", "Imaging");
        saveDepartment("Cardiology", "Heart");

        mockMvc.perform(get("/api/v1/departments")
                        .param("sort", "name,asc")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Cardiology"))
                .andExpect(jsonPath("$.content[1].name").value("Radiology"));
    }

    @Test
    void getDepartmentByIdReturnsDepartment() throws Exception {
        Department department = saveDepartment("Neurology", "Brain and nervous system");

        mockMvc.perform(get("/api/v1/departments/{id}", department.getDepartmentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(department.getDepartmentId().toString()))
                .andExpect(jsonPath("$.name").value("Neurology"));
    }

    @Test
    void updateDepartmentReturnsUpdatedDepartment() throws Exception {
        Department department = saveDepartment("Cardio", "Initial");
        DepartmentRequest request = new DepartmentRequest("Cardiology", "Handles heart care");

        mockMvc.perform(put("/api/v1/departments/{id}", department.getDepartmentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(department.getDepartmentId().toString()))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.description").value("Handles heart care"));
    }

    @Test
    void deleteDepartmentReturnsDeletedPayload() throws Exception {
        Department department = saveDepartment("Oncology", "Cancer care");

        mockMvc.perform(delete("/api/v1/departments/{id}", department.getDepartmentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(department.getDepartmentId().toString()))
                .andExpect(jsonPath("$.name").value("Oncology"));
    }

    @Test
    void getDepartmentByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/departments/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
