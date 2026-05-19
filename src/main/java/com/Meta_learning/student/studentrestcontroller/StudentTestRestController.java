package com.Meta_learning.student.studentrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestSubmitDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestSubmitRequestDTO;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestEntity;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTestService.KDTTestService;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/KDT")
public class StudentTestRestController {

    private final KDTPartservice kdtPartservice;
    private final KDTTestService kdtTestService;
    private final StudentPermissionService studentPermissionService;
    private final KDTSessionService kdtSessionService;

    // 답변을 저장하는 메서드
    @PostMapping("/{kdtSessionId}/test/submit/{kdtTestId}")
    public ResponseEntity<?>postKdtTestSubmit(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                              @AuthenticationPrincipal UserEntity user,
                                              @RequestBody KDTTestSubmitRequestDTO requestDTO) {
        try{
            if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTTestEntity kdtTestEntity = kdtTestService.findKdtTestEntityById(kdtTestId);
            // 시험을 찾을 수 없다면 실패 반환
            if(kdtTestEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "시험을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
            }

            Long partId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
            // 해당 참석자가 해당 문제를 풀었는지 확인
            List<KDTTestSubmitDTO> submits = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, partId);

            if(submits!=null && !submits.isEmpty()){
                ResponseMessage response = new ResponseMessage("failure", "이미 작성한 답이 있습니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 200 OK 응답
            }

            KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = kdtTestDTO.getKdtTestStartDate();
            LocalDateTime endTime = kdtTestDTO.getKdtTestEndDate();

            // 시작 시간 이전 혹은 종료 시간 이후인 경우
            if (now.isBefore(startTime) || now.isAfter(endTime)){
                ResponseMessage response = new ResponseMessage("failure", "제출 가능한 시간이 아닙니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 200 OK 응답
            }

            requestDTO.setKdtPartId(partId);
            kdtTestService.kdtTestSubmitSave(requestDTO);

            kdtTestService.kdtTestGradingAutoSave(kdtTestId, partId);

            return ResponseEntity.status(HttpStatus.OK).body("test"); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // 답변을 수정하는 메서드
    @PostMapping("/{kdtSessionId}/test/submit/update/{kdtTestId}")
    public ResponseEntity<?> postKdtTestSubmitUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                                     @AuthenticationPrincipal UserEntity user,
                                                     @RequestBody KDTTestSubmitRequestDTO requestDTO) {
        try{
            if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Long kdtPartId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
            List<KDTTestSubmitDTO> submits = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, kdtPartId);

            if(submits==null || submits.isEmpty()){
                ResponseMessage response = new ResponseMessage("failure", "기존에 작성한 답이 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
            }
            kdtTestService.kdtTestSubmitUpdate(requestDTO);
            kdtTestService.kdtTestGradingAutoUpdate(kdtTestId, kdtPartId);

            return ResponseEntity.status(HttpStatus.OK).body("test"); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }


}
