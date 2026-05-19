package com.Meta_learning.KDT.KDTservice.KDTAttService;


import com.Meta_learning.KDT.KDTDTO.KDTAttDTO.KDTAttDTO;
import com.Meta_learning.KDT.KDTDTO.KDTAttListDTO.KDTAttListDTO;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface KDTAttService {



    //날짜와 세션으로 출석부 목록 가져오기
    List<KDTAttListDTO> findKdtAttList(LocalDate date, Long sessionId, Long kdtPartId);

    // 참석자의 모든 출석부 가져오기
    List<KDTAttDTO> findKdtAtt(Long kdtPartId);

    // 참가자 id와 날짜로 기존 출석부가 있는지 확인
    Boolean findKdtAtt(Long kdtPartId, LocalDate kdtAttDate);

    // kdtAttId로 조회하기
    KDTAttEntity findKdtAttById(Long kdtAttID);

    // 출석부 새로 저장하기
    KDTAttEntity kdtAttSave(Long kdtSessionId, KDTAttDTO kdtAttDTO);

    // 출석부 업데이트 하기
    KDTAttEntity updateKdtAtt(Long kdtAttId, Long kdtSessionId, KDTAttDTO kdtAttDTO);

    // 출석부 삭제하기
    void deleteAtt(Long kdtAttId);

    // 통계용(월별 : 모두 /나이별/ 성별)
    List<Map<String, Object>> getAllMonthlyAttendanceStats(Long kdtSessionId);
    List<Map<String, Object>> getAllMonthlyAttendanceStatsWithAgeGroups(Long kdtSessionId);
    List<Map<String, Object>> getAllMonthlyAttendanceStatsWithGenderGroups(Long kdtSessionId);

    // 통계용(월별 : 모두 / 나이별 / 성별)
    List<Map<String, Object>> getAllWeeklyAttendanceStats(Long kdtSessionId);
    List<Map<String, Object>> getAllWeeklyAttendanceStatsWithAgeGroups(Long kdtSessionId);
    List<Map<String, Object>> getAllWeeklyAttendanceStatsWithGenderGroups(Long kdtSessionId);

//    Map<String, Map<String, Integer>> getKdtAttendanceStats(Long kdtSessionId);
//    Map<String, Map<String, Map<String, Integer>>> getKdtAttendanceStatsWithAgeGroups(Long kdtSessionId);

}
