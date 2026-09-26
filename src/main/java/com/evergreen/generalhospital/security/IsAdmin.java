package com.evergreen.generalhospital.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

// simple validator to only allow admins to access AdminController

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@PreAuthorize("hasRole('ADMIN')")
public @interface IsAdmin {
}
