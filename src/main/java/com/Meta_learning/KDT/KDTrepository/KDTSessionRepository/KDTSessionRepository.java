package com.Meta_learning.KDT.KDTrepository.KDTSessionRepository;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KDTSessionRepository extends JpaRepository<KDTSessionEntity, Long> {

    // 코스 ID와 세션 번호가 존재하는지 확인
    boolean existsByKdtCourseEntity_KdtCourseIdAndKdtSessionNum(Long kdtCourseId, int kdtSessionNum);

    // 'kdtCourseEntity'의 'kdtCourseId'를 기준으로 최신 세션 번호를 조회
    Optional<KDTSessionEntity> findTopByKdtCourseEntity_KdtCourseIdOrderByKdtSessionNumDesc(Long courseId);

    // 주어진 courseId에 대해 세션이 존재하는지 확인하는 메서드
    boolean existsByKdtCourseEntity_KdtCourseId(Long courseId);

    // courseId에 해당하는 세션 리스트를 가져오는 메서드
    List<KDTSessionEntity> findByKdtCourseEntity_KdtCourseId(Long courseId);

    // 세션 상태가 주어진 상태인 세션들만 조회
    Page<KDTSessionEntity> findByKdtSessionStatus(KDTSessionStatus status, Pageable pageable);

    // 세션 상태와 제목을 기준으로 검색
    Page<KDTSessionEntity> findByKdtSessionStatusAndKdtSessionTitleContaining(KDTSessionStatus status, String title, Pageable pageable);

    // 세션 상태와 카테고리를 기준으로 검색
    Page<KDTSessionEntity> findByKdtSessionStatusAndKdtSessionCategoryContaining(KDTSessionStatus status, String category, Pageable pageable);

    // 주어진 currentDate보다 종료일이 지난 세션의 상태를 업데이트 (진행 중 -> 완료)
    @Modifying
    @Transactional
    @Query("UPDATE KDTSessionEntity s " +
            "SET s.kdtSessionStatus = :status " +
            "WHERE s.kdtSessionEndDate < :currentDate AND s.kdtSessionStatus != :status")
    int updateSessionsToFinished(LocalDate currentDate, KDTSessionStatus status);


    // 시작일이 오늘이거나 지났고 종료일이 안 지난 세션 상태를 'ONGOING'으로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE KDTSessionEntity s " +
            "SET s.kdtSessionStatus = :status " +
            "WHERE s.kdtSessionStartDate <= :currentDate AND s.kdtSessionEndDate >= :currentDate AND s.kdtSessionStatus != :status")
    int updateSessionsToOngoing(LocalDate currentDate, KDTSessionStatus status);


    // 시작일이 아직 안 온 세션(ONGOING으로 잘못 설정된 경우) 상태를 'WAITING'으로 복구
    @Modifying
    @Transactional
    @Query("UPDATE KDTSessionEntity s " +
            "SET s.kdtSessionStatus = :status " +
            "WHERE s.kdtSessionStartDate > :currentDate AND s.kdtSessionStatus = com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.ONGOING")
    int updateSessionsToWaiting(LocalDate currentDate, KDTSessionStatus status);

    // 제목과 카테고리가 모두 있을 경우 (검색 + 조인 + 시작 날짜 오름차순 + 상태 추가)
    @Query("SELECT s FROM KDTSessionEntity s JOIN FETCH s.kdtCourseEntity c WHERE s.kdtSessionTitle LIKE %:searchName% AND s.kdtSessionCategory LIKE %:searchCategory% AND s.kdtSessionStatus != com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.FINISHED ORDER BY CASE WHEN s.kdtSessionStatus = com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.ONGOING THEN 1 ELSE 0 END, s.kdtSessionStartDate ASC")
    Page<KDTSessionEntity> findByKdtSessionTitleContainingAndKdtSessionCategoryContaining(
            @Param("searchName") String searchName,
            @Param("searchCategory") String searchCategory,
            Pageable pageable);

    // 제목만 있을 경우 (시작 날짜 오름차순 + 상태 추가)
    @Query("SELECT s FROM KDTSessionEntity s JOIN FETCH s.kdtCourseEntity c WHERE s.kdtSessionTitle LIKE %:searchName% AND s.kdtSessionStatus != com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.FINISHED ORDER BY CASE WHEN s.kdtSessionStatus = com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.ONGOING THEN 1 ELSE 0 END, s.kdtSessionStartDate ASC")
    Page<KDTSessionEntity> findByKdtSessionTitleContaining(
            @Param("searchName") String searchName,
            Pageable pageable);

    // 카테고리만 있을 경우 (시작 날짜 오름차순 + 상태 추가)
    @Query("SELECT s FROM KDTSessionEntity s JOIN FETCH s.kdtCourseEntity c WHERE s.kdtSessionCategory LIKE %:searchCategory% AND s.kdtSessionStatus != com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.FINISHED ORDER BY CASE WHEN s.kdtSessionStatus = com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.ONGOING THEN 1 ELSE 0 END, s.kdtSessionStartDate ASC")
    Page<KDTSessionEntity> findByKdtSessionCategoryContaining(
            @Param("searchCategory") String searchCategory,
            Pageable pageable);

    // 모든 항목을 검색할 경우 (제목, 카테고리 모두 없는 경우) + 시작 날짜 오름차순 + 상태 추가
    @Query("SELECT s FROM KDTSessionEntity s JOIN FETCH s.kdtCourseEntity c WHERE s.kdtSessionStatus != com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.FINISHED ORDER BY CASE WHEN s.kdtSessionStatus = com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus.ONGOING THEN 1 ELSE 0 END, s.kdtSessionStartDate ASC")
    Page<KDTSessionEntity> findAll(Pageable pageable);


    // sessionId로 객체를 찾는 메서드
    KDTSessionEntity findByKdtSessionId(Long KdtSessionId);

    // 중복되지 않는 카테고리 목록을 가져오는 쿼리
    @Query("SELECT DISTINCT s.kdtSessionCategory FROM KDTSessionEntity s WHERE s.kdtSessionCategory IS NOT NULL")
    List<String> findDistinctCategories();
}
