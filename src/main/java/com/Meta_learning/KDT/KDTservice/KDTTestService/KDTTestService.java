package com.Meta_learning.KDT.KDTservice.KDTTestService;


import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.*;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestGradingEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestItemEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestSubmitEntity;

import java.util.List;
import java.util.Map;

public interface KDTTestService {
    // 시험을 저장하는 메서드
    KDTTestEntity kdtTestSave(KDTTestDTO kdtTestDTO);
    // 시험 문제를 저장하는 메서드
    List<KDTTestItemEntity> kdtTestItemSave(List<KDTTestItemDTO> kdtTestItemDTOS, Long kdtTestId);

    // 시험을 조회하는 메서드
    KDTTestDTO findKdtTestDTOTestById(Long kdtTestId);
    KDTTestEntity findKdtTestEntityById(Long kdtTestId);

    // TestId로 문제를 조회하는 메서드
    List<KDTTestItemDTO> findKdtTestItemDTOByKdtTestId(Long kdtTestId);
    List<KDTTestItemEntity> findKdtTestItemEntityByKdtTestId(Long kdtTestId);

    // 시험 목록을 조회하는 메서드
    List<KDTTestListDTO> findKdtTestListBySessionId(Long kdtSessionId);

    // 작성자의 시험 목록을 조회하는 메서드
    List<KDTTestListDTO> findKdtTestListBySessionIdAndUserId(Long kdtSessionId, Long userId);

    // 특정 시험의 testList를 조회하는 메서드
    KDTTestListDTO findKdtTestListByTestId(Long kdtTestId);

    // 시험 답안 목록을 조회하는 메서드
    List<KDTTestSubmitListDTO> findKdtTestSubmitListByTestId(Long kdtSessionId, Long kdtTestId);

    // 학생이 시험 목록을 조회하는 메서드
    List<KDTTestStudentListDTO> findKdtTestStudentListBySessionId(Long kdtSessionId, Long kdtPartId);

    // 시험을 삭제하는 메서드
    void deleteTest(Long kdtTestId);

    // 시험을 업데이트 하는 메서드
    KDTTestEntity kdtTestUpdate(KDTTestDTO kdtTestDTO);

    // 시험 문제를 업데이트 하는 메서드
    List<KDTTestItemEntity> kdtTestItemUpdate(Long kdtTestItemId, List<KDTTestItemDTO> kdtTestItemDTOs);

    // 시험 문제를 삭제하는 메서드
    void kdtTestItemDelete(List<Long> deleteIds);

    // 시험 답안을 등록하는 메서드
    List<KDTTestSubmitEntity> kdtTestSubmitSave(KDTTestSubmitRequestDTO requestDTO);

    // 제출한 답안을 확인하는 메서드
    List<KDTTestSubmitDTO> findKdtTestSubmitDTOByTestIdAndPartId(Long kdtTestId, Long kdtPartId);

    // 시험 답안을 업데이트 하는 메서드
    List<KDTTestSubmitEntity> kdtTestSubmitUpdate(KDTTestSubmitRequestDTO requestDTO);

    // 시험 채점을 저장하는 메서드
    List<KDTTestGradingEntity> kdtTestGradingAutoSave(Long kdtTestId, Long kdtPartId);

    // 시험 채점을 자동으로 업데이트 하는 메서드
    List<KDTTestGradingEntity> kdtTestGradingAutoUpdate(Long kdtTestId, Long kdtPartId);

    // 여러 시험 채점을 자동으로 업데이트 하는 메서드
    List<KDTTestGradingEntity> kdtTestGradingsAutoUpdate(Long kdtTestId);

    // 시험 채점을 수동으로 업데이트 하는 메서드
    List<KDTTestGradingEntity> kdtTestGradingUpdate(KDTTestGradingRequestDTO requestDTO);
    // 시험 점수를 확인하는 메서드
    List<KDTTestGradingDTO> findKdtTestGradingDTOByTestIdAndPartId(Long kdtTestId, Long kdtPartId);

    // 통계
    List<Map<String, Object>> getAllStudentsTestStats(Long kdtSessionId);
    List<Map<String, Object>> getAgeGroupStudentsTestStats(Long kdtSessionId);
    List<Map<String, Object>> getGenderGroupStudentsTestStats(Long kdtSessionId);


}
