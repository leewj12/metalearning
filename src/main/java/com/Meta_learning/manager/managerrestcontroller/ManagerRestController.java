package com.Meta_learning.manager.managerrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTStaffDTO.KDTStaffDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartStatus;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.manager.managerservice.ManagerService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userdto.UserPartDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/KDT")
public class ManagerRestController {

    @Autowired
    private StaffPermissionService staffPermissionService;

    private final BoardService boardService;
    private final ManagerService managerService;  // ManagerService 주입
    private final KDTService kdtService;
    private final KDTPartservice kdtPartservice;
    private final UserService userService;

    // 배정된 매니저의 국비 과정 불러오기
    @GetMapping("/list")  // 경로 수정
    public ResponseEntity<?> getCourseList(@AuthenticationPrincipal UserEntity user) {
        try {
            // 로그인한 사용자의 userId를 가져옴
            Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기

            // 해당 사용자의 국비 과정 리스트를 불러오는 서비스 호출
            List<KDTCourseDTO> courseList = managerService.getCoursesByUser(userId);  // ManagerService에서 과정 리스트 호출

            return ResponseEntity.ok(courseList);

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
        }
    }

    //배정된 매니저의 회차정보 가져오는 메서드임
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getSessionsByCourseId(@PathVariable Long courseId, @AuthenticationPrincipal UserEntity user) {
        try {
            Long userId = user.getUserId();

            // 매니저 권한 확인
            if (!staffPermissionService.hasAccessToCourse(courseId)) {
                // 권한이 없으면 실패 메시지 반환
                ResponseMessage response = new ResponseMessage("failure", "회차에 등록된 매니저가 아닙니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // courseId와 userId로 관련된 세션 목록 가져오기
            List<KDTSessionDTO> sessions = kdtService.getManagerSessionsByCourseId(courseId, userId);

            return ResponseEntity.ok(sessions);

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    // 강사 삭제 처리
    @DeleteMapping("/session/{kdtSessionId}/staff/delete")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long kdtSessionId,
                                              @RequestBody KDTStaffDTO kdtStaffDTO) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(kdtSessionId)) {

                // 사용자 ID와 세션 ID로 강사 삭제
                try {
                    boolean instructorDeleted = kdtService.deleteInstructor(kdtSessionId, kdtStaffDTO.getUserId());

                    if (instructorDeleted) {
                        // 성공적인 삭제 메시지
                        ResponseMessage response = new ResponseMessage("success", "강사가 삭제되었습니다.");
                        return ResponseEntity.ok().body(response);
                    } else {
                        // 강사를 찾을 수 없는 경우
                        ResponseMessage response = new ResponseMessage("fail", "해당 강사를 찾을 수 없습니다.");
                        return ResponseEntity.status(404).body(response);
                    }
                } catch (Exception e) {
                    // 예외 처리
                    ResponseMessage response = new ResponseMessage("error", "이미 참여중인 강사는 삭제가 불가능합니다.");
                    return ResponseEntity.status(500).body(response);
                }
            } else {
                // 매니저 권한이 없는 경우
                ResponseMessage response = new ResponseMessage("fail", "해당 회차에 대한 접근 권한이 없습니다.");
                return ResponseEntity.status(403).body(response);  // 403 Forbidden
            }
        } else {
            // 인증되지 않은 경우
            ResponseMessage response = new ResponseMessage("fail", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(401).body(response);  // 401 Unauthorized
        }
    }


    // 신청상담 삭제하는 메서드
    @DeleteMapping("/{sessionId}/appconsult/{consultId}/delete")
    public ResponseEntity<ResponseMessage> deleteConsult(
            @PathVariable Long sessionId,
            @PathVariable Long consultId) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(sessionId)) {

                try {
                    // 상담 삭제 로직 호출
                    boolean isDeleted = kdtService.deleteConsult(consultId, sessionId);

                    ResponseMessage response;

                    if (isDeleted) {
                        // 삭제 성공 시 응답
                        response = new ResponseMessage("success", "신청 상담이 삭제되었습니다.");
                        return ResponseEntity.ok(response);  // 200 OK 응답
                    } else {
                        // 삭제 실패 시 응답
                        response = new ResponseMessage("failure", "상담 삭제 실패.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found 응답
                    }

                } catch (Exception e) {
                    // 예외 발생 시 에러 메시지와 함께 INTERNAL_SERVER_ERROR 응답 반환
                    ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error 응답
                }

            } else {
                // 매니저 권한이 없는 경우
                ResponseMessage response = new ResponseMessage("failure", "매니저 권한이 필요합니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }
        } else {
            // 인증되지 않은 사용자
            ResponseMessage response = new ResponseMessage("failure", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized 응답
        }
    }


    // 상담 상태 수정 API
    @PutMapping("/{sessionId}/appconsult/{consultId}/updateStatus")
    public ResponseEntity<?> updateConsultStatus(
            @PathVariable("sessionId") String sessionId,
            @PathVariable("consultId") String consultId,
            @RequestBody Map<String, String> body // 요청 본문에서 새로운 상태를 받음
    ) {


        String newStatus = body.get("newStatus"); // newStatus를 요청 본문에서 가져옵니다.

        // 상태 업데이트 로직
        boolean isUpdated = kdtService.updateStatus(sessionId, consultId, newStatus);

        if (isUpdated) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "상담 상태가 수정되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "상담 상태 수정에 실패했습니다."));
        }
    }



    //
    @GetMapping("{sid}/part/list")
    public ResponseEntity<?> getParticipantList(@PathVariable("sid") Long sessionId) {
        try {
            // 매니저인지 확인하는 로직
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

    //회차별 참여한 유저 삭제하는 메서드
    @DeleteMapping("{sessionId}/part/delete/{kdtPartId}")
    public ResponseEntity<ResponseMessage> deleteUserPart(@PathVariable("sessionId") Long sessionId,
                                                          @PathVariable("kdtPartId") Long kdtPartId) {
        try {

            // 매니저인지 확인하는 로직
            if (!staffPermissionService.hasAccessToSession(sessionId)) {
                ResponseMessage response = new ResponseMessage("failure", "매니저 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
            }


            // 세션에 해당하는 참가자 리스트 조회
            boolean isDeleted = kdtPartservice.deleteUserPart(sessionId, kdtPartId);

            if (isDeleted) {
                // 삭제 성공 시
                ResponseMessage response = new ResponseMessage("success", "참가자가 성공적으로 삭제되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response);  // 200 OK와 함께 반환
            } else {
                // 삭제 실패 시
                ResponseMessage response = new ResponseMessage("failure", "해당 참가자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // 404 Not Found 응답
            }
        } catch (Exception e) {
            // 예외 발생 시
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 Internal Server Error 응답
        }
    }


    //게시글 삭제하는메서드
    @DeleteMapping("/delete/material/{postId}")
    public ResponseEntity<ResponseMessage> deleteMaterial(@PathVariable("postId") Long postId) {
        try {

            // 해당 게시글을 삭제
            boolean isDeleted = boardService.boardDelete(postId);

            if (isDeleted) {
                // 삭제 성공 시
                ResponseMessage response = new ResponseMessage("success", "강의 자료 게시글이 성공적으로 삭제되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response);  // 200 OK와 함께 반환
            } else {
                // 삭제 실패 시
                ResponseMessage response = new ResponseMessage("failure", "해당 강의 자료 게시글을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // 404 Not Found 응답
            }
        } catch (Exception e) {
            // 예외 발생 시
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 Internal Server Error 응답
        }
    }


    //매니처 회차 상세보기
    @GetMapping("/session/{sessionid}")
    public ResponseEntity<?> getSessionsBySessionId(@PathVariable Long sessionid) {
        try {

            // 매니저인지 확인하는 로직
            if (!staffPermissionService.hasAccessToSession(sessionid)) {
                ResponseMessage response = new ResponseMessage("failure", "매니저 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
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

    // 회차별 참여한 유저 수정하는 메서드
    @PutMapping("{sessionId}/part/update/{kdtPartId}")
    public ResponseEntity<ResponseMessage> updateUserPart(@PathVariable("sessionId") Long sessionId,
                                                          @PathVariable("kdtPartId") Long kdtPartId,
                                                          @RequestBody Map<String, String> body) {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            ResponseMessage response = new ResponseMessage("failure", "매니저 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 응답
        }

        String newStatus = body.get("newStatus");  // 참여 상태
        String newEmploymentStatus = body.get("newEmploymentStatus"); // 취업 상태

        KDTPartStatus status = (newStatus != null) ? KDTPartStatus.valueOf(newStatus) : null;
        Boolean employmentStatus = (newEmploymentStatus != null) ? Boolean.valueOf(newEmploymentStatus) : null;

        try {
            // 세션에 해당하는 참가자 상태 수정
            boolean isUpdated = kdtPartservice.updateUserPart(sessionId, kdtPartId, status, employmentStatus);

            if (isUpdated) {
                // 수정 성공 시
                ResponseMessage response = new ResponseMessage("success", "참가자의 상태 정보가 수정되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response);  // 200 OK와 함께 반환
            } else {
                // 수정 실패 시
                ResponseMessage response = new ResponseMessage("failure", "해당 참가자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // 404 Not Found 응답
            }
        } catch (Exception e) {
            // 예외 발생 시
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 Internal Server Error 응답
        }
    }

    //회차에 등록된 매니저 강사 정보 보내주는 메서드임
    @GetMapping("{sessionId}/staff/list")
    public ResponseEntity<Map<String, Object>> getStaffList(@PathVariable Long sessionId) {
        Map<String, Object> response = new HashMap<>();

        // 매니저 권한 확인
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없으면 실패 메시지 반환
            response.put("status", "failure");
            response.put("message", "회차에 등록된 매니저가 아닙니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            // 이미 등록된 강사 명단 가져오기
            List<UserSignUpDTO> registeredInstructors = userService.userRegisteredInstructors(sessionId);
            // 이미 등록된 매니저 명단 가져오기
            List<UserSignUpDTO> registeredManagers = userService.userRegisteredManager(sessionId);

            // 응답에 강사 및 매니저 리스트 추가
            response.put("instructors", registeredInstructors);
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






}
