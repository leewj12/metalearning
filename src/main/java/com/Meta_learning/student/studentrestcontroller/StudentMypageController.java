package com.Meta_learning.student.studentrestcontroller;

import com.Meta_learning.admin.admindashboarddto.UserStatusDTO;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentMypageController {

    @GetMapping("/api/student/user/profile")
    public ResponseEntity<UserStatusDTO> getUserProfile(@AuthenticationPrincipal UserEntity user) {
        try {
            // 로그인한 사용자 정보가 담긴 UserEntity를 사용
            UserStatusDTO userStatus = new UserStatusDTO();
            userStatus.setUserEmail(user.getUserEmail());
            userStatus.setUserRole(user.getUserRole());
            userStatus.setName(user.getName());

            // 정상적으로 유저 정보를 반환
            return ResponseEntity.status(HttpStatus.OK).body(userStatus); // 200 OK 응답
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserStatusDTO()); // 빈 객체 반환
        }
    }


}
