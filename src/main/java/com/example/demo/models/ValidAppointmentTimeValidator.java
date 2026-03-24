package com.example.demo.models;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAppointmentTimeValidator implements ConstraintValidator<ValidAppointmentTime, Appointment> {

    @Override
    public void initialize(ValidAppointmentTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Appointment appointment, ConstraintValidatorContext context) {
        if (appointment == null) return true;

        if (appointment.getStart() == null || appointment.getEnd() == null) return true;

        return appointment.getEnd().isAfter(appointment.getStart());
    }
}
