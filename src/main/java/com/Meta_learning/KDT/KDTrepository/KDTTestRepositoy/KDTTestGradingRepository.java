package com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy;

import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestGradingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KDTTestGradingRepository extends JpaRepository<KDTTestGradingEntity, Long> {

    List<KDTTestGradingEntity> findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtTestSubmitEntity_KdtPartEntity_kdtPartId(Long kdtTestId, Long kdtPartId);
    List<KDTTestGradingEntity> findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(Long kdtTestId);
    void deleteByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestItemIdIn(List<Long> deleteIds);
    KDTTestGradingEntity findByKdtTestGradingId(Long kdtTestGradingId);

}
