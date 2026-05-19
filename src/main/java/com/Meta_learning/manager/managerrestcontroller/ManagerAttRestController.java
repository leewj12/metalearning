package com.Meta_learning.manager.managerrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTAttDTO.KDTAttDTO;
import com.Meta_learning.KDT.KDTDTO.KDTAttListDTO.KDTAttListDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttEntity;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttStatus;
import com.Meta_learning.KDT.KDTservice.KDTAttService.KDTAttService;
import com.Meta_learning.KDT.KDTservice.KDTConsultService.KDTConsultService;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTrainService.KDTTrainService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/KDT")
public class ManagerAttRestController {

    private final KDTAttService kdtAttService;
    private final KDTSessionService kdtSessionService;
    private final KDTTrainService kdtTrainService;
    private final KDTConsultService kdtConsultService;
    private final StaffPermissionService staffPermissionService;

    // 회차별 출석부 목록 가져오기
    @GetMapping("/{kdtSessionId}/att/list")
    public ResponseEntity<?> getKdtAttentionList(@PathVariable Long kdtSessionId,
                                                 @RequestParam(required = false) LocalDate date){
        try {
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // 특정 회차의 모든 참석자의 오늘날짜(LocalDate.now()) 출석부 가져오기
            if (date ==null){
                date = LocalDate.now();
            }

            List<KDTAttListDTO> kdtAttListDTOs = kdtAttService.findKdtAttList(date, kdtSessionId, null);

//            // 회차 정보가 없으면 실패 메시지와 함께 OK 응답 반환
//            if (kdtAttListDTOs == null || kdtAttListDTOs.isEmpty()) {
//                ResponseMessage response = new ResponseMessage("failure", "출석부 정보가 없습니다.");
//                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
//            }

            KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

            // 응답에 포함할 Map 생성
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("attendanceList", kdtAttListDTOs);
            responseMap.put("KDTSessionDTO", kdtSessionDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답

        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    // 참가자 별 출석부 가져오기
    @GetMapping("/{kdtSessionId}/att/detail/{kdtPartId}")
    public ResponseEntity<?> getKdtAttentionDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtPartId){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // kdtPartId로 출석정보 불러오기
            List<KDTAttDTO> kdtAttDTOs = kdtAttService.findKdtAtt(kdtPartId);

//            // 출석 정보가 없으면 실패 메시지와 함께 OK 응답 반환
//            if (kdtAttDTOs == null || kdtAttDTOs.isEmpty()) {
//                ResponseMessage response = new ResponseMessage("failure", "출석부 정보가 없습니다.");
//                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
//            }

            List<KDTAttListDTO> kdtAttListDTO = kdtAttService.findKdtAttList(LocalDate.now(), kdtSessionId, kdtPartId);
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

    // 새로운 출석부 등록하는 코드
    @PostMapping("/{kdtSessionId}/att/new")
    public ResponseEntity<?> postKdtAttention(@PathVariable Long kdtSessionId, @RequestBody KDTAttDTO kdtAttDTO){
        try {
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

//        {
//                "kdtPartId": 10001,           // 필수
//                "kdtAttDate": "2025-01-03",   //필수
//                "kdtAttEntryTime": "2025-01-03T09:00:00",
//                "kdtAttExitTime": "2025-01-03T17:00:00",
//                "kdtAttLeaveStart": "2025-01-03T12:00:00",
//                "kdtAttLeaveEnd": "2025-01-03T13:00:00",
//                "kdtAttStatus": "ARRIVAL"
//        }
            Long kdtPartId = kdtAttDTO.getKdtPartId();  // 참가자 아이디
            LocalDate kdtAttDate = kdtAttDTO.getKdtAttDate();

            // 이미 같은 날에 등록 된 정보가 있는 지 확인
            if(kdtAttService.findKdtAtt(kdtPartId, kdtAttDate)){
                ResponseMessage response = new ResponseMessage("failure", "기존 출석부가 이미 있습니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 200 OK 응답
            }

            // 데이터 저장하기
            KDTAttEntity kdtAttEntity = kdtAttService.kdtAttSave(kdtSessionId, kdtAttDTO);

            if(kdtAttEntity != null){
                // Error라면 출석부 시간에 오류가 있었다는 뜻
                if(kdtAttEntity.getKdtAttStatus() == KDTAttStatus.ERROR){
                    ResponseMessage response = new ResponseMessage("failure", "출석부 시간에 오류가 있습니다. 확인하세요.");
                    return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
                }
                // 출석부 등록 성공
                return getKdtAttentionDetail(kdtSessionId, kdtPartId);
            }

            // kdtAttEntity 가 NULL이라는 것은 저장에 실패했다는 뜻임
            ResponseMessage response = new ResponseMessage("failure", "출석부 등록에 실패했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 200 OK 응답

        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    @PutMapping("/{kdtSessionId}/att/update/{kdtAttId}")
    public ResponseEntity<?> updateKdtAttention(@PathVariable Long kdtSessionId, @PathVariable Long kdtAttId, @RequestBody KDTAttDTO kdtAttDTO){
        try {
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTAttEntity kdtAttEntity = kdtAttService.findKdtAttById(kdtAttId);
            // 출석부를 찾을 수 없다면 실패 반환
            if(kdtAttEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "출석부를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
            }


            // 업데이트 하기
            kdtAttEntity = kdtAttService.updateKdtAtt(kdtAttId, kdtSessionId, kdtAttDTO);
            if(kdtAttEntity != null){
                // 업데이트 후 Error인지 확인
                if(kdtAttService.findKdtAttById(kdtAttId).getKdtAttStatus() == KDTAttStatus.ERROR){
                    ResponseMessage response = new ResponseMessage("failure", "출석부 시간에 오류가 있습니다. 확인하세요.");
                    return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
                }
                // 출석부 업데이트에 성공하면 출석부를 고친 참가자의 출석부 목록으로 이동
                Long kdtPartId = kdtAttEntity.getKdtPartEntity().getKdtPartId();  // 참가자 아이디
                return getKdtAttentionDetail(kdtSessionId, kdtPartId);
            }

            // 업데이트 자체가 안됨.
            ResponseMessage response = new ResponseMessage("failure", "출석부 수정에 실패했습니다");
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
        } catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    @DeleteMapping("/{kdtSessionId}/att/delete/{kdtAttId}")
    public ResponseEntity<?> deleteKdtAttention(@PathVariable Long kdtSessionId, @PathVariable Long kdtAttId){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            KDTAttEntity kdtAttEntity = kdtAttService.findKdtAttById(kdtAttId);
            // 출석부를 찾을 수 없다면 실패 반환
            if(kdtAttEntity == null){
                ResponseMessage response = new ResponseMessage("failure", "출석부를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK 응답
            }
            // 출석부를 지우는 코드
            kdtAttService.deleteAtt(kdtAttId);

            // 성공하면 지운 참가자의 출석부로 이동
            Long kdtPartId = kdtAttEntity.getKdtPartEntity().getKdtPartId();  // 참가자 아이디
            return getKdtAttentionDetail(kdtSessionId, kdtPartId);
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }



    // 출석부 통계 가져오기
    @GetMapping("/{kdtSessionId}/att/attchart")
    public ResponseEntity<?> getKdtAttentionChart(@PathVariable Long kdtSessionId,
                                                  @RequestParam(required = false) LocalDate date){
        try{
            // 접근 권한 확인하기
            if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
                ResponseMessage response = new ResponseMessage("failure", "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 모든 월별 출석 통계 가져오기

            List<Map<String, Object>> totalMonthlyAttendanceStats = kdtAttService.getAllMonthlyAttendanceStats(kdtSessionId);
            List<Map<String, Object>> totalDayOfTheWeekStats = kdtAttService.getAllWeeklyAttendanceStats(kdtSessionId);

            List<Map<String, Object>> ageGroupMonthlyAttendanceStats = kdtAttService.getAllMonthlyAttendanceStatsWithAgeGroups(kdtSessionId);
            List<Map<String, Object>> ageGroupDayOfTheWeek = kdtAttService.getAllWeeklyAttendanceStatsWithAgeGroups(kdtSessionId);

            List<Map<String, Object>> genderGroupMonthlyAttendanceStats = kdtAttService.getAllMonthlyAttendanceStatsWithGenderGroups(kdtSessionId);
            List<Map<String, Object>> genderGroupDayOfTheWeek = kdtAttService.getAllWeeklyAttendanceStatsWithGenderGroups(kdtSessionId);

            // 모든 통계
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("totalMonthly", totalMonthlyAttendanceStats);
            responseMap.put("totalDay", totalDayOfTheWeekStats);

            // 나이 그룹별 출석 통계
            responseMap.put("ageGroupMonthly", ageGroupMonthlyAttendanceStats);
            responseMap.put("ageGroupDay", ageGroupDayOfTheWeek);

            // 성별 그룹별 출석 통계
            responseMap.put("genderGroupMonthly", genderGroupMonthlyAttendanceStats);
            responseMap.put("genderGroupDay", genderGroupDayOfTheWeek);

            return ResponseEntity.status(HttpStatus.OK).body(responseMap); // 200 OK 응답
        }catch (Exception e){
            ResponseMessage response = new ResponseMessage("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }
}
