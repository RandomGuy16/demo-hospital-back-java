package com.evergreen.generalhospital.models.appointment;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// implementation of ValidApointmentTimeValidator
public class ValidAppointmentTimeValidator implements ConstraintValidator<ValidAppointmentTime, Appointment> {

    // default, initializer
    @Override
    public void initialize(ValidAppointmentTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    // necessary function, isValid
    @Override
    public boolean isValid(Appointment appointment, ConstraintValidatorContext context) {
        if (appointment == null) return true;

        // no null timestamps
        if (appointment.getStart() == null || appointment.getEnd() == null) return false;

        return appointment.getEnd().isAfter(appointment.getStart());  // end must be after start
    }
}
