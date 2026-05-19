package com.Meta_learning.KDT.KDTrepository.KDTAppConsultRepository;


import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsult;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KDTAppConsultRepository extends JpaRepository<KDTAppConsult, Long> {

    // 이름과 상태를 기준으로 검색
    @Query("SELECT k FROM KDTAppConsult k WHERE " +
            "(k.kdtAppConsultName LIKE %:searchName% OR :searchName IS NULL) AND " +
            "k.kdtSessionEntity.kdtSessionId = :sessionId AND " +
            "(k.kdtAppConsultStatus = :status OR :status IS NULL) " +
            "ORDER BY CASE WHEN k.kdtAppConsultStatus = com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus.PENDING THEN 0 ELSE 1 END, k.kdtAppCreatedAt DESC")
    Page<KDTAppConsult> findByKdtAppConsultNameContainingAndKdtSessionEntity_KdtSessionIdAndKdtAppConsultStatus(
            String searchName, Long sessionId, KDTAppConsultStatus status, Pageable pageable);

    // 세션 ID와 상태를 기준으로 검색
    @Query("SELECT k FROM KDTAppConsult k WHERE " +
            "k.kdtSessionEntity.kdtSessionId = :sessionId AND " +
            "(k.kdtAppConsultStatus = :status OR :status IS NULL) " +
            "ORDER BY CASE WHEN k.kdtAppConsultStatus = com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus.PENDING THEN 0 ELSE 1 END, k.kdtAppCreatedAt DESC")
    Page<KDTAppConsult> findByKdtSessionEntity_KdtSessionIdAndKdtAppConsultStatus(
            Long sessionId, KDTAppConsultStatus status, Pageable pageable);

    // 세션 ID만 기준으로 검색
    @Query("SELECT k FROM KDTAppConsult k WHERE " +
            "k.kdtSessionEntity.kdtSessionId = :sessionId " +
            "ORDER BY CASE WHEN k.kdtAppConsultStatus = com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus.PENDING THEN 0 ELSE 1 END, k.kdtAppCreatedAt DESC")
    Page<KDTAppConsult> findByKdtSessionEntity_KdtSessionId(
            Long sessionId, Pageable pageable);

    // 이름과 세션 ID 기준으로 검색
    @Query("SELECT k FROM KDTAppConsult k WHERE k.kdtAppConsultName LIKE %:searchName% AND k.kdtSessionEntity.kdtSessionId = :sessionId ORDER BY CASE WHEN k.kdtAppConsultStatus = com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus.PENDING THEN 0 ELSE 1 END, k.kdtAppCreatedAt DESC")
    Page<KDTAppConsult> findByKdtAppConsultNameContainingAndKdtSessionEntity_KdtSessionId(
            String searchName, Long sessionId, Pageable pageable);

    // 세션 ID와 상담 ID를 기반으로 상담 정보 조회
    @Query("SELECT k FROM KDTAppConsult k WHERE k.kdtSessionEntity.kdtSessionId = :sessionId AND k.kdtAppConsultId = :consultId")
    Optional<KDTAppConsult> findBySessionIdAndConsultId(@Param("sessionId") Long sessionId, @Param("consultId") Long consultId);

    // 세션 ID와 상담 ID로 조회
    Optional<KDTAppConsult> findByKdtSessionEntity_KdtSessionIdAndKdtAppConsultId(Long sessionId, Long consultId);
}




