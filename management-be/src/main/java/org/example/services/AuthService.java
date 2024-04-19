package org.example.services;

import io.jsonwebtoken.JwtException;
import org.example.dto.SignUpDto;
import org.example.entities.User;
import org.example.repositories.PasswordResetTokenRepository;
import org.example.repositories.UserRepository;
import org.example.utils.EnhancedUserDetails;
import org.example.utils.PasswordPolicyValidator;
import org.example.utils.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    UserRepository repository;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByLogin(username);
        return user;
    }

    public UserDetails signUp(SignUpDto data) throws Exception {

        log.info("Attempting to sign up user with username: {}", data.login());
        if (repository.findByLogin(data.login()) != null) {
            throw  new JwtException("Username already exists");
        }

        if(repository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Email already in use");
        }

        List<String> passwordErrors = passwordPolicyValidator.validatePassword(data.password());
        if (!passwordErrors.isEmpty()) {
            StringJoiner joiner = new StringJoiner(" ");
            passwordErrors.forEach(joiner::add);
            log.error("Password validation errors: {}", joiner.toString());
            throw new Exception("Password does not meet the policy requirements: " + joiner);
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
//        User newUser = new User(data.login(), encryptedPassword, data.role());
        User newUser = new User(
                data.login(),
                encryptedPassword,
                data.firstName(),
                data.lastName(),
                data.email(),
                data.phone(),
                data.role()
        );
        log.info("User signed up successfully with username: {}", data.login());

        return repository.save(newUser);

    }
}
