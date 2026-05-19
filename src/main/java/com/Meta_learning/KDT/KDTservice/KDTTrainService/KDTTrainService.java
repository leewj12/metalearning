package com.Meta_learning.KDT.KDTservice.KDTTrainService;


import com.Meta_learning.KDT.KDTDTO.KDTTrainDTO.KDTTrainDTO;
import com.Meta_learning.KDT.KDTentity.KDTTrainEntity.KDTTrainEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface KDTTrainService {
    // 세션에 소속된 강사의 담당자Id와 이름 가져오기
    Map<Long, String> findRegisteredInstr(Long sessionId);

    // 세션 id와 날짜로 기존 훈련일지가 있는지 확인
    Long findKdtTrain(Long kdtSessionId, LocalDate kdtTrainDate);

    // TrainId로 조회하기
    KDTTrainDTO findKdtTrainById(Long kdtTrainId);

    // 훈련일지 저장하기
    KDTTrainEntity kdtTrainSave(KDTTrainDTO kdtTrainDTO);

    // 훈련일지 삭제하기
    void deleteTrain(Long kdtTrainId);

    // 훈련일지 리스트 불러오기
    List<KDTTrainDTO> findKdtTrainListBySessionId(Long kdtSessionId);
}
