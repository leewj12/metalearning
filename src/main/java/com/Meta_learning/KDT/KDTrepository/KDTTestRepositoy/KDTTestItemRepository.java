package com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy;


import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTTestItemRepository extends JpaRepository<KDTTestItemEntity, Long> {
    // ID로 엔티티를 찾는 메서드
    KDTTestItemEntity findByKdtTestItemId(Long kdtTestItemId);

    List<KDTTestItemEntity> findByKdtTestEntity_kdtTestId(Long kdtTestId);

    // TestItem 삭제
    void deleteByKdtTestItemIdIn(List<Long> kdtTestItemIds);

}
