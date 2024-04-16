package org.example.services;

import jakarta.transaction.Transactional;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.example.utils.EnhancedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<String> findUsernameByLogin(String login) {
        UserDetails userDetails = userRepository.findByLogin(login);
        return Optional.ofNullable(userDetails).map(UserDetails::getUsername);
    }

    public Optional<String> findEmailByLogin(String login) {
        UserDetails userDetails = userRepository.findByLogin(login);
        if (userDetails instanceof EnhancedUserDetails) {
            return Optional.ofNullable(((EnhancedUserDetails) userDetails).getEmail());
        }
        return Optional.empty();
    }

    public Optional<User> updateEmail(Long userId, String newEmail) {
        return userRepository.findById(userId).map(user -> {
            user.setEmail(newEmail);
            return userRepository.save(user);
        });
    }

    public Optional<User> updateUsername(Long userId, String newUsername) {
        return userRepository.findById(userId).map(user -> {
            user.setLogin(newUsername);
            return userRepository.save(user);
        });
    }
}
