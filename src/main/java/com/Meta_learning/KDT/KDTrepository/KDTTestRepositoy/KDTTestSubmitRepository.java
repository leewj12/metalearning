package com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy;

import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestSubmitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTTestSubmitRepository extends JpaRepository<KDTTestSubmitEntity, Long> {
    List<KDTTestSubmitEntity> findByKdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtPartEntity_kdtPartId(Long kdtTestId, Long kdtPartId);
    List<KDTTestSubmitEntity> findByKdtTestItemEntity_KdtTestEntity_kdtTestId(Long kdtTestId);
    void deleteByKdtTestItemEntity_KdtTestItemIdIn(List<Long> deleteIds);

    @Query("SELECT COUNT(DISTINCT s.kdtPartEntity) " +
            "FROM KDTTestSubmitEntity s " +
            "WHERE s.kdtTestItemEntity.kdtTestEntity.kdtTestId = :kdtTestId")
    long countDistinctParticipants(@Param("kdtTestId") Long kdtTestId);

}
