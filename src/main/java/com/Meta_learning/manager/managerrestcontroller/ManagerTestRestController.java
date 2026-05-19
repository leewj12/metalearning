package com.Meta_learning.manager.managerrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.*;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestEntity;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTestService.KDTTestService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/KDT")
public class ManagerTestRestController {

    private final KDTTestService kdtTestService;
    private final KDTSessionService kdtSessionService;
    private final StaffPermissionService staffPermissionService;

    // 시험 등록
    @PostMapping("/{kdtSessionId}/test")
    public ResponseEntity<?> postKdtTest(@PathVariable Long kdtSessionId, @RequestBody KDTTestDetailsDTO kdtTestDetailsDTO,
                                         @AuthenticationPrincipal UserEntity user) {
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 데이터 셋팅하기
            KDTTestDTO kdtTestDTO = kdtTestDetailsDTO.getKdtTest();

            if(kdtTestDTO.getKdtTestStartDate().isAfter(kdtTestDTO.getKdtTestEndDate())){
                ResponseMessage response = new ResponseMessage("failure", "시작시간은 종료 시간 이전이어야 합니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 200 OK 응답
            }

            kdtTestDTO.setUserId(user.getUserId());
            kdtTestDTO.setKdtTestCreatedAt(LocalDateTime.now());
            List<KDTTestItemDTO> kdtTestItemDTOs = kdtTestDetailsDTO.getKdtTestItems();

            // 데이터 저장하기
            KDTTestEntity kdtTestEntity= kdtTestService.kdtTestSave(kdtTestDTO);
            Long kdtTestId = kdtTestEntity.getKdtTestId();
            kdtTestService.kdtTestItemSave(kdtTestItemDTOs, kdtTestId);

            return getKdtTestList(kdtSessionId); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }


    // 시험 수정(업데이트)
    @PostMapping("/{kdtSessionId}/test/update/{kdtTestId}")
    public ResponseEntity<?> postKdtTestUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,@RequestBody KDTTestDetailsDTO kdtTestDetailsDTO) {
        try {
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // test를 업데이트
            KDTTestDTO kdtTestDTO = kdtTestDetailsDTO.getKdtTest();

            KDTTestEntity kdtTestEntity = kdtTestService.findKdtTestEntityById(kdtTestId);

            // 시험을 찾을 수 없다면 실패 반환
            if(kdtTestEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "수정할 시험을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
            }

            if(kdtTestDTO.getKdtTestStartDate().isAfter(kdtTestDTO.getKdtTestEndDate())){
                ResponseMessage response = new ResponseMessage("failure", "시작시간은 종료 시간 이전이어야 합니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 200 OK 응답
            }

            kdtTestDTO.setKdtTestUpdatedAt(LocalDateTime.now());
            kdtTestService.kdtTestUpdate(kdtTestDTO);

            // 시험 문제와 관련된 답안, 채점 삭제
            List<Long> kdtTestItemDeleteIds = kdtTestDetailsDTO.getKdtTestItemDeleteId();
            kdtTestService.kdtTestItemDelete(kdtTestItemDeleteIds);

            // 새로운 문제 업데이트
            List<KDTTestItemDTO> kdtTestItemDTOs = kdtTestDetailsDTO.getKdtTestItems();
            kdtTestService.kdtTestItemUpdate(kdtTestId, kdtTestItemDTOs);
            kdtTestService.kdtTestGradingsAutoUpdate(kdtTestId);
            // 새로운 채점 업데이트

            return ResponseEntity.status(HttpStatus.OK).body("test");
        } catch (Exception e) {
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // 시험 삭제
    @DeleteMapping("/{kdtSessionId}/test/delete/{kdtTestId}")
    public ResponseEntity<?> deleteKdtTest(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTTestEntity kdtTestEntity = kdtTestService.findKdtTestEntityById(kdtTestId);

            // 시험을 찾을 수 없다면 실패 반환
            if(kdtTestEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "삭제할 시험을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
            }

            // 시험과 시험문제(+답안과 채점)를 지우는 코드
            kdtTestService.deleteTest(kdtTestId);

            // 성공하면 성공했다고 반환
            return ResponseEntity.status(HttpStatus.OK).body("");
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // (학생이 작성한) 답안 수정
    @PostMapping("/{kdtSessionId}/test/submit/update/{kdtTestId}/{kdtPartId}")
    public ResponseEntity<?> postKdtTestSubmitUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                                     @PathVariable Long kdtPartId,
                                                     @RequestBody KDTTestSubmitRequestDTO requestDTO) {
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            List<KDTTestSubmitDTO> submits = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, kdtPartId);

            if(submits==null || submits.isEmpty()){
                ResponseMessage response = new ResponseMessage("failure", "학생이 작성한 답안이 삭제되었거나 존재하지 않습니다.");
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

    // 답안에 대한 채점 수정
    @PostMapping("/{kdtSessionId}/test/grading/update/{kdtTestId}/{kdtPartId}")
    public ResponseEntity<?> postKdtTestgradingUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                                      @PathVariable Long kdtPartId,@AuthenticationPrincipal UserEntity user,
                                                      @RequestBody KDTTestGradingRequestDTO requestDTO) {
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            kdtTestService.kdtTestGradingUpdate(requestDTO);

            return ResponseEntity.status(HttpStatus.OK).body("test"); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // React로 시험 목록에 보내는 코드
    @GetMapping("/{kdtSessionId}/test/list")
    public ResponseEntity<?> getKdtTestList(@PathVariable Long kdtSessionId){
        // 접근 권한 확인하기
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // 시험
        // 시험 이름/ 출제일 / 시작날짜 / 마감 날짜 / 출제자 이름 / 실제 응시자 수 / 총 응시자수
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        List<KDTTestListDTO> kdtTestListDTOs = kdtTestService.findKdtTestListBySessionId(kdtSessionId);

        // 응답에 포함할 Map 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("kdtTestListDTOs", kdtTestListDTOs);
        responseMap.put("KDTSessionDTO", kdtSessionDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
    }

    // React로 답안 목록에 보내는 코드
    @GetMapping("/{kdtSessionId}/test/submit/{kdtTestId}/list")
    public ResponseEntity<?> getKdtTestSubmitList(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId){

        // 접근 권한 확인하기
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        KDTTestEntity kdtTestEntity = kdtTestService.findKdtTestEntityById(kdtTestId);
        // 시험을 찾을 수 없다면 실패 반환
        if(kdtTestEntity == null){
            ResponseMessage response = new ResponseMessage("failure", "삭제할 시험을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
        }

        // 시험
        // 응시자 수/ 총 응시자수
        // 시험 이름/ 학생 이름 /첫제출 / 수정날짜 / 점수 /
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        List<KDTTestSubmitListDTO> kdtTestSubmitListDTOs = kdtTestService.findKdtTestSubmitListByTestId(kdtSessionId, kdtTestId);
        KDTTestListDTO kdtTestListDTO = kdtTestService.findKdtTestListByTestId(kdtTestId);
        // 응답에 포함할 Map 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("kdtTestListDTO", kdtTestListDTO);
        responseMap.put("kdtTestSubmitListDTOs", kdtTestSubmitListDTOs);
        responseMap.put("KDTSessionDTO", kdtSessionDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
    }


    @GetMapping("/{kdtSessionId}/test/testchart")
    public ResponseEntity<?> getKdtTestChart(@PathVariable Long kdtSessionId) {
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 모든 성적
            List<Map<String, Object>> totalTestStats = kdtTestService.getAllStudentsTestStats(kdtSessionId);
            // 나이별 성적
            List<Map<String, Object>> ageGroupTestStats = kdtTestService.getAgeGroupStudentsTestStats(kdtSessionId);
            // 성별 성적
            List<Map<String, Object>> genderGroupTestStats = kdtTestService.getGenderGroupStudentsTestStats(kdtSessionId);

            // 모든 통계
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("totalTestStats", totalTestStats);
            responseMap.put("ageGroupTestStats", ageGroupTestStats);
            responseMap.put("genderGroupTestStats", genderGroupTestStats);


            return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }
}
