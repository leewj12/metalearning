package com.Meta_learning.ip.repository;

import com.Meta_learning.ip.entity.KDTIpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTIpRepository extends JpaRepository<KDTIpEntity, Long> {
    // 여기에는 필요한 추가적인 쿼리 메서드를 작성할 수 있습니다.
    // 예시:
    // Optional<KDTIpEntity> findByKdtIpAddress(String ipAddress);

    // 세션 ID로 KDTIpEntity 목록을 조회하는 메서드
    List<KDTIpEntity> findByKdtSessionEntity_KdtSessionId(Long sessionId);
}
