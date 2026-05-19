package com.Meta_learning.security;


import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
//        log.info("User {} logged in at {}", userEmail, LocalDateTime.now());

        // 사용자 정보 조회
        UserEntity user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 로그인 시간 갱신 (세터 대신 withUserLastLogin 사용)
        UserEntity updatedUser = user.withUserLastLogin(LocalDateTime.now());
        userRepository.save(updatedUser);  // DB에 저장

        // 역할에 따라 리디렉션
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));
        boolean isInstructor = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_INSTRUCTOR"));
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"));

        if (isAdmin) {
            response.sendRedirect("/view/admin/dashboard");
        } else if (isManager) {
            response.sendRedirect("/view/manager/dashboard");
        } else if (isInstructor) {
            response.sendRedirect("/view/instr/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}
