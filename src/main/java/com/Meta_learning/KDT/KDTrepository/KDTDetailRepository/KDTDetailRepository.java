package com.Meta_learning.KDT.KDTrepository.KDTDetailRepository;

import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KDTDetailRepository extends JpaRepository<KDTDetailEntity,Long> {
    // SessionId로 KDTDetailEntity를 조회
    Optional<KDTDetailEntity> findByKdtSessionEntity_KdtSessionId(Long kdtSessionId);

    // Content를 기준으로 조회 (필요하다면)
    Optional<KDTDetailEntity> findByKdtDetailContent(String content);

}
