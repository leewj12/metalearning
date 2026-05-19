package com.Meta_learning.manager.managerrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTConsultDTO.KDTConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTrainDTO.KDTTrainDTO;
import com.Meta_learning.KDT.KDTservice.KDTAttService.KDTAttService;
import com.Meta_learning.KDT.KDTservice.KDTConsultService.KDTConsultService;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTrainService.KDTTrainService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/KDT")
public class ManagerKyRestController {

    private final KDTAttService kdtAttService;
    private final KDTSessionService kdtSessionService;
    private final KDTTrainService kdtTrainService;
    private final KDTConsultService kdtConsultService;
    private final StaffPermissionService staffPermissionService;

    @GetMapping("/{kdtSessionId}/train/list")
    public ResponseEntity<?> getKdtTrainList(@PathVariable Long kdtSessionId){

        // 접근 권한 확인하기
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // 특정 회차의 모든 훈련일지 가져오기
        List<KDTTrainDTO> kdtTrainDTOS = kdtTrainService.findKdtTrainListBySessionId(kdtSessionId);

        // 회차 정보가 없으면 실패 메시지와 함께 OK 응답 반환
        if (kdtTrainDTOS == null || kdtTrainDTOS.isEmpty()) {
            ResponseMessage response = new ResponseMessage("failure", "훈련일지 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        // 응답에 포함할 Map 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("trainList", kdtTrainDTOS);
        responseMap.put("KDTSessionDTO", kdtSessionDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
    }


    @DeleteMapping("/{kdtSessionId}/train/delete/{kdtTrainId}")
    public ResponseEntity<?> deleteKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTTrainDTO trainDTO = kdtTrainService.findKdtTrainById(kdtTrainId);
            // 훈련 일지를 찾을 수 없다면 실패 반환
            if(trainDTO == null){
                ResponseMessage response = new ResponseMessage("failure", "훈련일지를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            // 출석부를 지우는 코드
            kdtTrainService.deleteTrain(kdtTrainId);

            // 성공하면 목록으로 이동

            return getKdtTrainList(kdtSessionId);
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }



    @DeleteMapping("/{kdtSessionId}/consult/delete/{kdtConsultId}")
    public ResponseEntity<?> deleteKdtConsult(@PathVariable Long kdtSessionId, @PathVariable Long kdtConsultId,
                                              @AuthenticationPrincipal UserEntity user){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTConsultDTO consultDTO = kdtConsultService.findKdtConsultById(kdtConsultId);
            // 상담 일지를 찾을 수 없다면 실패 반환
            if(consultDTO == null){
                ResponseMessage response = new ResponseMessage("failure", "상담일지를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 200 OK 응답
            }

            // 본인이 작성하지 않은 게시글을 지우려고 시도할 경우
            if(!consultDTO.getUserId().equals(user.getUserId())){
                ResponseMessage response = new ResponseMessage("failure", "상담일지를 작성한 본인만 삭제가 가능합니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 200 OK 응답
            }
            // 출석부를 지우는 코드
            kdtConsultService.deleteConsult(kdtConsultId);

            // 성공하면 목록으로 이동
            return ResponseEntity.status(HttpStatus.OK).body("");
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

}
