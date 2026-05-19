package com.Meta_learning.manager.managerrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.admin.admindashboarddto.UserStatusDTO;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerDashboardRestController {

    private final UserService userService;
    private final StaffPermissionService staffPermissionService;
    private final KDTPartservice kdtPartservice;

    //매니저한테 유저 리스트 보내는 메서드 관리자는 제외
    @GetMapping("/api/manager/users/list")
    public ResponseEntity<?> UserList() {
        try {

            List<UserSignUpDTO> useralladminsexcluded = userService.allAdminsExcluded(); // 유저 정보 가져오기

            return ResponseEntity.ok(useralladminsexcluded);

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 INTERNAL_SERVER_ERROR 응답
        }
    }

    //회차에 참여한 참여자 인원 보내주는 메서드
    @GetMapping("/api/manager/part/{sessionId}/count")
    public ResponseEntity<?> getStudentCount(@PathVariable("sessionId") Long sessionId) {
        try {
            // 권한 체크: 세션에 대한 접근 권한이 없는 경우 403 응답
            if (!staffPermissionService.hasAccessToSession(sessionId)) {
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }

            // 세션 ID로 학생 수를 가져오는 서비스 메소드 호출
            KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

            // studentCount가 null이면 오류 메시지 반환
            if (studentCount == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("학생 정보가 없습니다!"); // 500 Internal Server Error 응답
            }

            // 학생 수가 0일 경우
            if (studentCount.getStudentCount() == 0) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("참여자가 없습니다."); // 200 OK 응답
            }

            // 정상적으로 유저 정보를 반환
            return ResponseEntity.status(HttpStatus.OK).body(studentCount); // 200 OK 응답
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("오류가 발생했습니다: " + e.getMessage()); // 500 Internal Server Error 응답
        }
    }


    //매니저 정보 보여주는 메서드
    @GetMapping("/api/manager/user/profile")
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




