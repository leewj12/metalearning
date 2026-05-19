package com.Meta_learning.student.studentrestcontroller;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.student.studentpermissionservice.StudentService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.utility.ResponseMessage;
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

            // 회차 정보가 없으면 실패 메시지와 함께 OK 응답 반환
            if (sessions.isEmpty()) {
                ResponseMessage response = new ResponseMessage("failure", "회차 정보가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 200 OK 응답
            }

            // 회차 정보가 있으면 성공 메시지와 함께 회차 리스트 반환
            return ResponseEntity.status(HttpStatus.OK).body(sessions); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 OK 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
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

            // 세션이 없으면 실패 메시지 반환
            if (sessions.isEmpty()) {
                ResponseMessage response = new ResponseMessage("failure", "아이디 정보가 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }

            // 세션이 있으면 첫 번째 세션의 ID를 가져옵니다.
            Long sessionId = sessions.get(0).getKdtSessionId();

            // 성공적으로 세션 정보를 반환
            return ResponseEntity.status(HttpStatus.OK).body(sessionId); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지를 반환합니다.
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }
    }



}





