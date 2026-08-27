package com.evergreen.generalhospital.models.useraccount;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUserAccountRoleLinkValidator implements ConstraintValidator<ValidUserAccountRoleLink, UserAccount> {
    @Override
    public void initialize(ValidUserAccountRoleLink constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserAccount value, ConstraintValidatorContext context) {
        // standard behaviour: if value is null then approve it, to avoid a pesky error
        if (value == null) return true;

        if (value.getRoles() == null || value.getRoles().isEmpty()) return false;

        boolean hasPerson = value.getPerson() != null;

        // Check invariants based on the assigned roles
        if (value.getRoles().contains(Role.ROLE_PATIENT) && !hasPerson) {
            return false;
        }
        if (value.getRoles().contains(Role.ROLE_PRACTITIONER) && !hasPerson) {
            return false;
        }
        return true;
    }
}
