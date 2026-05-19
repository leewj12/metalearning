package com.Meta_learning.student.studentrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTAttDTO.KDTAttDTO;
import com.Meta_learning.KDT.KDTDTO.KDTAttListDTO.KDTAttListDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttEntity;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttStatus;
import com.Meta_learning.KDT.KDTservice.KDTAttService.KDTAttService;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/KDT")
public class StudentAttRestController {

    private final StudentPermissionService studentPermissionService;
    private final KDTAttService kdtAttService;
    private final KDTPartservice kdtPartservice;
    private final KDTSessionService kdtSessionService;

    @GetMapping("/{kdtSessionId}/att/detail")
    public ResponseEntity<?> getKdtAttentionDetail(@PathVariable Long kdtSessionId,
                                                   @AuthenticationPrincipal UserEntity user){
        try{
            if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Long partId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
            // kdtPartId로 출석정보 불러오기
            List<KDTAttDTO> kdtAttDTOs = kdtAttService.findKdtAtt(partId);

//            // 출석 정보가 없으면 실패 메시지와 함께 OK 응답 반환
//            if (kdtAttDTOs == null || kdtAttDTOs.isEmpty()) {
//                ResponseMessage response = new ResponseMessage("failure", "출석부 정보가 없습니다.");
//                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
//            }

            List<KDTAttListDTO> kdtAttListDTO = kdtAttService.findKdtAttList(LocalDate.now(), kdtSessionId, partId);
            KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
            // 응답에 포함할 Map 생성
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("kdtAttDTOs", kdtAttDTOs);          // 일일 출석부
            responseMap.put("kdtAttListDTO", kdtAttListDTO);    // 총 출석 횟수
            responseMap.put("KDTSessionDTO", kdtSessionDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }


    // 새로운 출석부 등록하는 메서드
    @PostMapping("/{kdtSessionId}/att/new")
    public ResponseEntity<?> postKdtAttention(@PathVariable Long kdtSessionId, @RequestBody KDTAttDTO kdtAttDTO,
                                              @AuthenticationPrincipal UserEntity user){
        try {
//        {
//                "kdtPartId": 10001,           // 필수
//                "kdtAttDate": "2025-01-03",   //필수
//                "kdtAttEntryTime": "2025-01-03T09:00:00",
//                "kdtAttExitTime": "2025-01-03T17:00:00",
//                "kdtAttLeaveStart": "2025-01-03T12:00:00",
//                "kdtAttLeaveEnd": "2025-01-03T13:00:00",
//                "kdtAttStatus": "ARRIVAL"
//        }

            if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Long kdtPartId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
            LocalDate kdtAttDate = kdtAttDTO.getKdtAttDate();

            // 이미 같은 날에 등록 된 정보가 있는 지 확인
            if(kdtAttService.findKdtAtt(kdtPartId, kdtAttDate)){
                ResponseMessage response = new ResponseMessage("failure", "기존 출석부가 이미 있습니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            kdtAttDTO.setKdtPartId(kdtPartId);

            // 데이터 저장하기
            KDTAttEntity kdtAttEntity = kdtAttService.kdtAttSave(kdtSessionId, kdtAttDTO);

            if(kdtAttEntity != null){
                // Error라면 출석부 시간에 오류가 있었다는 뜻
                if(kdtAttEntity.getKdtAttStatus() == KDTAttStatus.ERROR){
                    ResponseMessage response = new ResponseMessage("failure", "출석부 시간에 오류가 있습니다. 확인하세요.");
                    return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
                }
                // 출석부 등록 성공
                return getKdtAttentionDetail(kdtSessionId, user);
            }

            // kdtAttEntity 가 NULL이라는 것은 저장에 실패했다는 뜻임
            ResponseMessage response = new ResponseMessage("failure", "출석부 등록에 실패했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 200 OK 응답

        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // 출석부를 업데이트 하는 메서드

    @PutMapping("/{kdtSessionId}/att/update/{kdtAttId}")
    public ResponseEntity<?> updateKdtAttention(@PathVariable Long kdtSessionId, @PathVariable Long kdtAttId, @RequestBody KDTAttDTO kdtAttDTO,
                                                @AuthenticationPrincipal UserEntity user){
        try {
            if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTAttEntity kdtAttEntity = kdtAttService.findKdtAttById(kdtAttId);
            // 출석부를 찾을 수 없다면 실패 반환
            if(kdtAttEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "출석부를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 200 OK 응답
            }

            Long kdtPartId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
            kdtAttDTO.setKdtPartId(kdtPartId);

            // 업데이트 하기
            kdtAttEntity = kdtAttService.updateKdtAtt(kdtAttId, kdtSessionId, kdtAttDTO);
            if(kdtAttEntity != null){
                // 업데이트 후 Error인지 확인
                if(kdtAttService.findKdtAttById(kdtAttId).getKdtAttStatus() == KDTAttStatus.ERROR){
                    ResponseMessage response = new ResponseMessage("failure", "출석부 시간에 오류가 있습니다. 확인하세요.");
                    return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
                }

                return getKdtAttentionDetail(kdtSessionId, user);
            }

            // 업데이트 자체가 안됨.
            ResponseMessage response = new ResponseMessage("failure", "출석부 수정에 실패했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }
}
