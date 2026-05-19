package com.Meta_learning.KDT.KDTrepository.KDTTrainRepository;

import com.Meta_learning.KDT.KDTentity.KDTTrainEntity.KDTTrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KDTTrainRepository extends JpaRepository<KDTTrainEntity,Long> {
    // 회차 정보와 날짜로 훈련일지를 찾는 메서드
    KDTTrainEntity findByKdtSessionEntity_KdtSessionIdAndKdtTrainDate(Long kdtSessionId, LocalDate kdtTrainDate);

    // 훈련일지ID(trainId)로 훈련일지를 찾는 메서드
    KDTTrainEntity findByKdtTrainId(Long kdtTrainId);

    List<KDTTrainEntity> findByKdtSessionEntity_KdtSessionIdOrderByKdtTrainDateDesc(Long kdtSessionId);

}
