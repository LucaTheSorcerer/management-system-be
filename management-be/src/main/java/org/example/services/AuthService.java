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

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;





    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;



//    public void initiatePasswordReset(String email) {
//        EnhancedUserDetails user = (EnhancedUserDetails) repository.findByEmail(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("No user found with email: " + email);
//        }
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken resetToken = new PasswordResetToken();
//        resetToken.setToken(token);
//        resetToken.setUser((User) user); // This cast is now safe because EnhancedUserDetails is guaranteed to be a User
//        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
//        passwordResetTokenRepository.save(resetToken);
//
//        sendResetTokenEmail(user.getEmail(), token);
//
//        // Send email logic
//    }



    public void completePasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is invalid or expired");
        }
        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        repository.save(user);

        // Invalidate the token after use
        passwordResetTokenRepository.delete(resetToken);
    }

//    private void sendResetTokenEmail(String email, String token) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the link below:\n" + "http://example.com/reset-password?token=" + token);
//        mailSender.send(message);
//    }

//    private void sendResetTokenEmail(String email, String token) {
//        String resetPasswordLink = "https://localhost:3000/reset-password?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the link below:\n" + resetPasswordLink);
//        mailSender.send(message);
//    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByLogin(username);
        return user;
    }

    public UserDetails signUp(SignUpDto data) throws Exception {

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
                data.role(),
                data.department()
        );
        return repository.save(newUser);

    }
}
