package com.evergreen.generalhospital.models.appointment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAppointmentTimeValidator.class)
public @interface ValidAppointmentTime {
    String message() default "end must be after start";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
