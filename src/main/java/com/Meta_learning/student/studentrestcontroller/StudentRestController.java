package com.Meta_learning.student.studentrestcontroller;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.student.studentpermissionservice.StudentService;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/KDT")

public class StudentRestController {


    @Autowired
    private  StudentPermissionService StudentPermissionService;

    private final StudentService studentService;  // ManagerService 주입

    //배정된 학생의 회차정보 가져오는 메서드임
    @GetMapping("/sessionlist")
    public ResponseEntity<?> getSessionsByCourseId(@AuthenticationPrincipal UserEntity user) {
        try {
            Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
            // 유저 ID로 세션 목록을 가져옴
            List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기

            return ResponseEntity.ok(sessions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //학생 아이디 찾는법
    @GetMapping("/studentid")
    public ResponseEntity<?> getStudentId(@AuthenticationPrincipal UserEntity user) {
        try {
            // 로그인한 사용자의 userId를 가져옵니다.
            Long userId = user.getUserId();

            // UserId를 이용해 해당 사용자의 세션 정보를 가져옵니다.
            List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

            if (sessions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(sessions.get(0).getKdtSessionId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}





