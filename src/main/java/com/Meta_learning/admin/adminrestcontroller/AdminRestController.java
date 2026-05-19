package com.Meta_learning.admin.adminrestcontroller;


import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTStaffDTO.KDTStaffDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartStatus;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.user.userdto.UserPartDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userservice.UserService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/KDT")
public class AdminRestController {

    private final KDTService kdtService;
    private final KDTPartservice kdtPartservice;
    private final BoardService boardService;
    private final UserService userService;

    //  과정선택하며 회치랑 과정명 가져와서 넣어주기
    @GetMapping("/session/getSessionNum")
    public ResponseEntity<Map<String, Object>> getSessionNum(@RequestParam Long courseId) {

        // 세션 번호와 과정명을 가져오는 서비스 호출
        Map<String, Object> sessionInfo = kdtService.getSessionNumAndCourseTitleByCourseId(courseId);

        // 세션 번호 증가
        int sessionNum = (int) sessionInfo.get("sessionNum");
        String courseTitle = (String) sessionInfo.get("courseTitle");
        int nextSessionNumber = sessionNum + 1;

        // 응답 데이터 준비
        Map<String, Object> response = new HashMap<>();
        response.put("sessionNum", nextSessionNumber); // 세션 번호
        response.put("courseTitle", courseTitle);     // 과정명

        // 응답 반환
        return ResponseEntity.ok(response);
    }

    //국비과정 불러오기
    @GetMapping("/list")
    public ResponseEntity<?> getCourseList() {
        try {
            List<KDTCourseDTO> courseall = kdtService.courseall();

            return ResponseEntity.ok(courseall);

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 OK 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
        }
    }


    //과정 삭제하는 메서드
    @DeleteMapping("/course/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            // 서비스에서 삭제 처리 (과정 삭제)
            boolean isDeleted = kdtService.deleteCourse(courseId);

            if (isDeleted) {
                // 삭제가 성공했을 때
                ResponseMessage response = new ResponseMessage("success", "삭제 성공했습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                // 삭제가 실패했을 때 (과정이 존재하지 않거나, 세션이 존재하면)
                // 먼저 세션 존재 여부를 확인하고 적절한 메시지 반환
                boolean hasSessions = kdtService.hasSessions(courseId);

                if (hasSessions) {
                    ResponseMessage response = new ResponseMessage("error", "회차가 존재하여 삭제할 수 없습니다.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // HTTP 403 Forbidden
                } else {
                    ResponseMessage response = new ResponseMessage("error", "삭제할 과정을 찾을 수 없습니다.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404 Not Found
                }
            }
        } catch (Exception e) {
            // 오류가 발생했을 때
            ResponseMessage response = new ResponseMessage("error", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getSessionsByCourseId(@PathVariable Long courseId) {
        try {
            // 세션 목록을 가져옴
            List<KDTSessionDTO> sessions = kdtService.getSessionsByCourseId(courseId);

            return ResponseEntity.ok(sessions);

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 함께 OK 응답 반환
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
        }
    }

    // 강사 삭제 처리
    @DeleteMapping("/session/{kdtSessionId}/staff/delete")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long kdtSessionId,
                                              @RequestBody KDTStaffDTO kdtStaffDTO) {

        // 사용자 ID와 세션 ID로 강사 삭제
        try {
            boolean instructorDeleted = kdtService.deleteInstructor(kdtSessionId, kdtStaffDTO.getUserId());

            if (instructorDeleted) {
                // 성공적인 삭제 메시지
                ResponseMessage response = new ResponseMessage("success", "강의 자료가 삭제 되었습니다");
                return ResponseEntity.ok().body(response);
            } else {
                // 강사를 찾을 수 없는 경우
                ResponseMessage response = new ResponseMessage("fail", "삭제할 자료가 없습니다 .");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            // 예외 처리
            ResponseMessage response = new ResponseMessage("error", "오류");
            return ResponseEntity.status(500).body(response);
        }
    }



    //세션 회차 찾는 정보
    @GetMapping("/session/{sessionid}")
    public ResponseEntity<?> getSessionsBySessionId(@PathVariable Long sessionid) {
        try {
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


    //신청상담 삭제하는 메서드
    @DeleteMapping("/{sessionId}/appconsult/{consultId}/delete")
    public ResponseEntity<ResponseMessage> deleteConsult(
            @PathVariable Long sessionId,
            @PathVariable Long consultId) {

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
    }

    //회차 삭제하는 메서드임
    @DeleteMapping("/session/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteSession(@PathVariable("id") Long sessionId) {
        try {
            // 세션 삭제 서비스 호출
            boolean isDeleted = kdtService.deleteSession(sessionId);

            if (isDeleted) {
                // 삭제 성공 시
                ResponseMessage response = new ResponseMessage("success", "회차가 삭제되었습니다.");
                return ResponseEntity.ok().body(response);
            } else {
                // 세션이 존재하지 않거나 삭제할 수 없는 경우
                ResponseMessage response = new ResponseMessage("fail", "회차가 존재하지 않거나 삭제할 수 없습니다.");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            // 예외 처리
            // 예외가 발생한 경우, 로깅을 추가하거나 세부 정보를 로그에 기록하는 것이 좋습니다.
            // 예: log.error("세션 삭제 중 오류 발생", e);
            ResponseMessage response = new ResponseMessage("error", "삭제 중 오류가 발생했습니다. 이미 진행중인 회차는 삭제할 수 없습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 상담 상태 수정 API
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

    //회차별 참여한 유저 상세 조회
    @GetMapping("/{sessionId}/part/list")
    public ResponseEntity<?> getUserPartList(@PathVariable("sessionId") Long sessionId) {
        try {
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


    //회차별 참여한 유저 수정하는 메서드
    // 회차별 참여한 유저 수정하는 메서드
    @PutMapping("{sessionId}/part/update/{kdtPartId}")
    public ResponseEntity<ResponseMessage> updateUserPart(@PathVariable("sessionId") Long sessionId,
                                                          @PathVariable("kdtPartId") Long kdtPartId,
                                                          @RequestBody Map<String, String> body) {

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



    //자료실 삭제하는 메서드임
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


    //회차에 참여한 정보 보여주는 곳
    @GetMapping("{sessionId}/staff/list")
    public ResponseEntity<Map<String, Object>> getStaffList(@PathVariable Long sessionId) {
        Map<String, Object> response = new HashMap<>();
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


