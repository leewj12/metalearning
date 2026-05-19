package com.Meta_learning.admin.adminrestcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTservice.KDTAttService.KDTAttService;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTestService.KDTTestService;
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
@RequestMapping("/api/admin/KDT")
public class AdminStatisticsRestController {

    private final KDTAttService kdtAttService;
    private final KDTTestService kdtTestService;
    private final KDTSessionService kdtSessionService;

    // 출석부 통계 가져오기
    @GetMapping("/{kdtSessionId}/att/attchart")
    public ResponseEntity<?> getKdtAttentionChart(@PathVariable Long kdtSessionId,
                                                 @RequestParam(required = false) LocalDate date){
        try{
            KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

            // 회차를 찾을 수 없다면 실패 반환
            if(kdtSessionDTO == null){
                ResponseMessage response = new ResponseMessage("failure", "국비 회차를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
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


    @GetMapping("/{kdtSessionId}/test/testchart")
    public ResponseEntity<?> getKdtTestChart(@PathVariable Long kdtSessionId) {
        try{
            KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

            // 회차를 찾을 수 없다면 실패 반환
            if(kdtSessionDTO == null){
                ResponseMessage response = new ResponseMessage("failure", "국비 회차를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 응답
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
