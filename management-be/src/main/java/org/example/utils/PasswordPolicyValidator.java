package org.example.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordPolicyValidator {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    public boolean isValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long.");
        }
        if (!password.matches(".*[0-9].*")) {
            errors.add("Password must contain at least one digit.");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter.");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            errors.add("Password must contain at least one special character (@#$%^&+=).");
        }
        if (password.matches(".*\\s.*")) {
            errors.add("Password must not contain any spaces.");
        }

        return errors;
    }
}