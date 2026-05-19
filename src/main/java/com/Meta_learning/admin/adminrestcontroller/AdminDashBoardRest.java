package com.Meta_learning.admin.adminrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.admin.admindashboarddto.UserCountDTO;
import com.Meta_learning.admin.admindashboarddto.UserRoleDTO;
import com.Meta_learning.admin.admindashboarddto.UserStatusDTO;
import com.Meta_learning.admin.adminservice.AdminService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminDashBoardRest {

    private final UserService userService;
    private final AdminService adminService;
    private final KDTPartservice kdtPartservice;

    //회원 전체 보내는 메서드
    @GetMapping("/user/list")
    public ResponseEntity<?> UserList() {
        try {

            List<UserSignUpDTO> userall = userService.userall(); // 유저 정보 가져오기

            // 유저 정보가 없으면 실패 메시지와 함께 OK 응답 반환
            if (userall.isEmpty()) {
                ResponseMessage response = new ResponseMessage("failure", "회원 정보가 없습니다!");
                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
            }

            return ResponseEntity.status(HttpStatus.OK).body(userall); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 INTERNAL_SERVER_ERROR 응답
        }

    }


    //유저권한변 통계
    @GetMapping("/user/role/list")
    public ResponseEntity<?> UserRoleList() {
        try {
            // 유저 정보 가져오기
            UserRoleDTO userRoleAll = adminService.userRoleAll();

            // 유저 정보가 없으면 실패 메시지와 함께 FORBIDDEN 응답 반환
            if (userRoleAll == null ||
                    (userRoleAll.getAdminTotal() == 0 && userRoleAll.getManagerTotal() == 0 &&
                            userRoleAll.getInstructorTotal() == 0 && userRoleAll.getStudentTotal() == 0)) {

                ResponseMessage response = new ResponseMessage("failure", "회원 정보가 없습니다!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }

            // 정상적으로 유저 정보를 반환
            return ResponseEntity.status(HttpStatus.OK).body(userRoleAll); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }
    }


    //총 인원수 보내주는 메서드
    @GetMapping("/user/count")
    public ResponseEntity<?> userCount() {
        try {
            // 유저 정보 가져오기
            List<UserCountDTO> userCount = adminService.userCount();

            // 정상적으로 유저 정보를 반환
            return ResponseEntity.status(HttpStatus.OK).body(userCount); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("오류가 발생했습니다: " + e.getMessage()); // 500 Internal Server Error 응답
        }
    }


    //회차에 참여한 참여자 인원 보내주는 메서드
    @GetMapping("/part/{sessionId}/count")
    public ResponseEntity<?> getStudentCount(@PathVariable("sessionId") Long sessionId) {
        try {
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

            // 정상적으로 학생 수를 반환
            return ResponseEntity.status(HttpStatus.OK).body(studentCount); // 200 OK 응답
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 500 내부 서버 오류 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("오류가 발생했습니다: " + e.getMessage()); // 500 Internal Server Error 응답
        }
    }


    // 로그인한 유저 정보 보여주는 메서드
    @GetMapping("/user/profile")
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
