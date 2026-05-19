package com.Meta_learning.student.studentpermissionservice;

import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class StudentPermissionService {

    @Autowired
    private KDTPartRepository kdtPartRepository;

    // 현재 로그인한 학생이 해당 세션회차에 접근할 권한이 있는지 확인
    public boolean hasAccessToSession(Long sessionId) {
        // SecurityContext에서 로그인한 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String userEmail = ((UserDetails) principal).getUsername();  // 로그인한 사용자의 이메일

            // 해당 세션에 사용자가 등록되어 있는지 확인 (UserEmail + SessionId로 체크)
            boolean hasAccess = kdtPartRepository.existsByUserEntity_UserEmailAndKdtSessionEntity_KdtSessionId(userEmail, sessionId);

            return hasAccess;
        }

        return false;  // 인증되지 않은 사용자
    }
}
