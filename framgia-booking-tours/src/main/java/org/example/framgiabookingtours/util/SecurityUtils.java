package org.example.framgiabookingtours.util;

import org.example.framgiabookingtours.dto.CustomUserDetails;
import org.example.framgiabookingtours.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {
    public static Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return Optional.of(customUserDetails.getUser());
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }
}
