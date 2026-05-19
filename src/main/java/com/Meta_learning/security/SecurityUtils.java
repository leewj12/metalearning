package com.Meta_learning.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUtils {

    // 사용자에게 특정 역할이 있는지 확인하는 메소드
    public static boolean hasRole(String role, Object principal) {
        if (principal instanceof UserDetails) {
            Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
            return authorities.stream().anyMatch(authority -> authority.getAuthority().equals(role));
        }
        return false;
    }
}