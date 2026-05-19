package com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy;


import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTTestRepository extends JpaRepository<KDTTestEntity, Long> {

    // ID로 엔티티를 찾는 메서드
    KDTTestEntity findByKdtTestId(Long kdtTestId);

    // 시험 삭제 메서드
    void deleteById(Long kdtTestId);

    // 회차ID로 Test를 찾는 메서드
    List<KDTTestEntity> findByKdtSessionEntity_KdtSessionId(Long kdtSessionId);

    // 회차ID와 작성자로 Test를 찾는 메서드
    List<KDTTestEntity> findByKdtSessionEntity_KdtSessionIdAndUserEntity_userId(Long kdtSessionId, Long userId);
}
