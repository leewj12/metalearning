package com.Meta_learning.KDT.KDTrepository.KDTAttRepository;


import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KDTAttRepository extends JpaRepository<KDTAttEntity,Long> {
    // PK로 출석부(KDTAttEntity) 찾기
    KDTAttEntity findByKdtAttId(Long kdtAttId);

    // 같은 SessionID를 갖는 출석부(KDTAttEntity) 찾기
    List<KDTAttEntity> findByKdtPartEntity_KdtSessionEntity_KdtSessionId(Long sessionId);

    // 참가자ID로 출석부(KDTAttEntity) 찾기
    List<KDTAttEntity> findByKdtPartEntity_KdtPartId(Long kdtPartId);

    //참석자ID와 출석일로 출석부(KDTAttEntity) 찾기
    KDTAttEntity findByKdtPartEntity_KdtPartIdAndKdtAttDate(Long kdtPartId, LocalDate kdtAttDate);

    // 삭제하기
    void deleteById(Long kdtAttId);


}
