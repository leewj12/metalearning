package com.Meta_learning.manager.managerservice;

import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
//스태프 등록된 매니저만 찾는 서비스임
@Service
public class StaffPermissionService {

    @Autowired
    private KDTStaffRepository kdtStaffRepository;

    // 현재 로그인한 매니저가 해당 세션회차에 접근할 권한이 있는지 확인
    public boolean hasAccessToSession(Long sessionId) {
        // SecurityContext에서 로그인한 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String userEmail = ((UserDetails) principal).getUsername();  // 로그인한 사용자의 이메일

            // 매니저및 강사가 해당 세션을 담당하는지 확인
            return kdtStaffRepository.existsByUserEntity_UserEmailAndKdtSessionEntity_KdtSessionId(userEmail, sessionId);
        }

        return false;  // 인증되지 않은 사용자
    }

    // 현재 로그인한 사용자가 해당 국비 과정에 접근할 권한이 있는지 확인
    public boolean hasAccessToCourse(Long kdtCourseId) {
        // SecurityContext에서 로그인한 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String userEmail = ((UserDetails) principal).getUsername();  // 로그인한 사용자의 이메일

            // 매니저가 해당 국비 과정에 속한 세션을 담당하는지 확인
            return kdtStaffRepository.existsByUserEntity_UserEmailAndKdtSessionEntity_KdtCourseEntity_KdtCourseId(userEmail, kdtCourseId);
        }

        return false;  // 인증되지 않은 사용자
    }



}
