package com.example.demo.models.useraccount;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUserAccountRoleLinkValidator.class)
public @interface ValidUserAccountRoleLink {
    String message() default "user must represent a patient or practitioner, but never none";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
