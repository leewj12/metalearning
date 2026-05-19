package com.Meta_learning.instr.instrrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTInstrSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.admin.admindashboarddto.UserStatusDTO;
import com.Meta_learning.course.courseservice.InstrService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userdto.UserPartDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InstrRestController {

    private final StaffPermissionService staffPermissionService;
    private final InstrService instrService;
    private final KDTService kdtService;
    private final UserService userService;
    private final KDTPartservice kdtPartservice;

    @GetMapping("/api/instr/user/profile")
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

    // 국비 과정 및 회차 정보 전달
    @GetMapping("/api/instr/KDT/list")
    public ResponseEntity<?> getCourseList(@AuthenticationPrincipal UserEntity user){
        try{
            Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기

            List<KDTInstrSessionDTO> courseList = instrService.getInstrSessionByUser(userId);
            return ResponseEntity.ok(courseList);
        }catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }

    }

    // 회차 상세 정보 조회
    @GetMapping("/api/instr/KDT/session/{sessionid}")
    public ResponseEntity<?> getSessionsBySessionId(@PathVariable Long sessionid) {
        try {
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(sessionid)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 세션 목록을 가져옴
            KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

            // 세션 정보가 없으면 실패 메시지와 함께 NOT_FOUND 응답 반환
            if (sessionsDetail == null) {
                ResponseMessage response = new ResponseMessage("failure", "회차 정보가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found 응답
            }

            // 회차 정보가 있으면 성공 메시지와 함께 회차 리스트 반환
            return ResponseEntity.status(HttpStatus.OK).body(sessionsDetail); // 200 OK 응답

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 INTERNAL_SERVER_ERROR 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }
    }

    // 회차 강사, 매니저 정보 전달
    @GetMapping("/api/instr/KDT/{sessionId}/staff/list")
    public ResponseEntity<Map<String, Object>> getStaffList(@PathVariable Long sessionId) {
        Map<String, Object> response = new HashMap<>();

        // 접근 권한 확인하기
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없으면 실패 메시지 반환
            response.put("status", "failure");
            response.put("message", "회차에 등록된 매니저가 아닙니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            // 이미 등록된 매니저 명단 가져오기
            List<UserSignUpDTO> registeredManagers = userService.userRegisteredManager(sessionId);

            // 응답에 강사 및 매니저 리스트 추가
            response.put("managers", registeredManagers);

            // 정상 처리된 경우 200 OK 응답
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 500 Internal Server Error 응답
            response.put("status", "error");
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //회차에 참여한 참여자 인원 보내주는 메서드
    @GetMapping("/api/instr/part/{sessionId}/count")
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

    // 등록된 학생을 조회하는 메서드
    @GetMapping("/api/instr/KDT/{sid}/part/list")
    public ResponseEntity<?> getParticipantList(@PathVariable("sid") Long sessionId) {
        try {
            // 접근권한을 확인하는 로직
            if (!staffPermissionService.hasAccessToSession(sessionId)) {
                ResponseMessage response = new ResponseMessage("failure", "매니저 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }

            // 세션에 해당하는 참가자 리스트 조회
            List<UserPartDTO> userPartDTO = kdtPartservice.userpartall(sessionId);

            return ResponseEntity.ok(userPartDTO != null ? userPartDTO : List.of());
        } catch (Exception e) {
            // 예외 발생 시
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }
    }
}
