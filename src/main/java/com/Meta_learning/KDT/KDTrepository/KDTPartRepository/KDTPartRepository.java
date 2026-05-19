package com.Meta_learning.KDT.KDTrepository.KDTPartRepository;

import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KDTPartRepository extends JpaRepository<KDTPartEntity,Long> {
    // 한 session(회차)의 모든 참석자 정보를 가져오는 메서드
    List<KDTPartEntity> findByKdtSessionEntity_KdtSessionId(Long kdtSessionId);

    // 한 참석자(partID)의 모든 참석자 정보를 가져오는 메서드
    KDTPartEntity findByKdtPartId(Long kdtPartId);

    // KDTSessionEntity의 KDTSessionId로 KDTPartEntity의 수를 세는 쿼리 메서드
    long countByKdtSessionEntity_KdtSessionId(Long kdtSessionId);

    KDTPartEntity findByKdtSessionEntity_KdtSessionIdAndUserEntity_userId(Long kdtSessionId, Long userId);

    // 특정 세션에 특정 유저가 이미 등록되었는지 확인하는 메서드
    boolean existsByKdtSessionEntity_KdtSessionIdAndUserEntity_UserId(Long kdtSessionId, Long userId);

    // 해당 이메일과 세션 ID로 사용자가 등록되어 있는지 확인
    boolean existsByUserEntity_UserEmailAndKdtSessionEntity_KdtSessionId(String userEmail, Long sessionId);

    // KDTSessionEntity의 kdtSessionId와 KDTPartEntity의 kdtPartId를 사용
    Optional<KDTPartEntity> findByKdtSessionEntity_KdtSessionIdAndKdtPartId(Long kdtSessionId, Long kdtPartId);


    // 해당 userId로 담당하는 세션 찾기
    List<KDTPartEntity> findByUserEntity_UserId(Long userId);


}
