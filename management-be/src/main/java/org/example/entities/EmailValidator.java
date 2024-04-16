package org.example.entities;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.utils.ValidEmail;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String emailField, ConstraintValidatorContext cxt) {
        return emailField != null && emailField.matches(".+@.+\\..+");
    }
}

