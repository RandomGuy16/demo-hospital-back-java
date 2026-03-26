package com.example.demo.models.useraccount;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUserAccountRoleLinkValidator implements ConstraintValidator<ValidUserAccountRoleLink, UserAccount> {
    @Override
    public void initialize(ValidUserAccountRoleLink constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserAccount value, ConstraintValidatorContext context) {
        if (value == null || value.getRole() == null) return true;

        boolean hasPatient = value.getPatient() != null;
        boolean hasPractitioner = value.getPractitioner() != null;

        return switch (value.getRole()) {
            case ROLE_PATIENT -> hasPatient && !hasPractitioner;
            case ROLE_PRACTITIONER -> hasPractitioner && !hasPatient;
            case ROLE_ADMIN, ROLE_RECEPTIONIST -> !hasPatient && !hasPractitioner;
        };
    }
}
