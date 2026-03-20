package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientPatchRequest(
    // switch from validations like @NotBlank to @Pattern to allow empty strings
    // and applying constrains on present values
    // https://stackoverflow.com/questions/5202222/how-to-make-a-string-field-optional-in-spring-mvc-rest-controller
    @Schema(example = "Jane")
    @Pattern(regexp = ".*\\S.*", message = "firstName must not be blank")
    String firstName,

    @Schema(example = "Doe")
    @Pattern(regexp = ".*\\S.*", message = "lastName must not be blank")
    String lastName,

    @Schema(example = "1234567890")
    @Pattern(regexp = "\\d{10}", message = "idNumber must be exactly 10 digits")
    String idNumber,

    @Schema(example = "1995-04-18")
    @Past(message = "dateOfBirth must be in the past") LocalDate dateOfBirth,

    @Schema(example = "female")
    @Pattern(regexp = ".*\\S.*", message = "gender must not be blank")
    String gender,

    @Schema(example = "+1 555 0100")
    @Pattern(regexp = ".*\\S.*", message = "phoneNumber must not be blank")
    @Size(min = 6, max = 20, message = "phoneNumber length must be between 6 and 20")
    String phoneNumber,

    @Schema(example = "jane.doe@example.com")
    @Pattern(regexp = ".*\\S.*", message = "contacts must not be blank")
    String contacts,

    @Schema(example = "123 Main St, Springfield")
    @Pattern(regexp = ".*\\S.*", message = "address must not be blank")
    String address) {
};
