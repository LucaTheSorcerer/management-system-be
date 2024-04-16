package org.example.utils;

import org.springframework.security.core.userdetails.UserDetails;

public interface EnhancedUserDetails extends UserDetails {
    String getEmail();
}
