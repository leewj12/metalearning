package com.Meta_learning.KDT.KDTrepository.KDTBoardrepository;

import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KDTBoardRepository extends JpaRepository<KDTBoardEntity, Long> {

    // 카테고리와 검색어로 필터링 + sessionId 필터링 (최신 업데이트 순으로 정렬)
    @Query("SELECT b FROM KDTBoardEntity b " +
            "LEFT JOIN b.userEntity u " +
            "WHERE b.kdtBoardCategory = :category " +
            "AND b.kdtSessionEntity.kdtSessionId = :sessionId " +
            "AND (b.kdtBoardTitle LIKE %:searchName% OR b.kdtBoardContent LIKE %:searchName%) " +
            "ORDER BY b.kdtBoardUpdatedAt DESC") // 최신 업데이트 순으로 정렬
    Page<KDTBoardEntity> findByKdtBoardCategoryAndSearchNameAndSessionId(
            @Param("category") KDTBoardCategory category,
            @Param("searchName") String searchName,
            @Param("sessionId") Long sessionId,
            Pageable pageable);

    // 카테고리와 sessionId로 필터링 (최신 업데이트 순으로 정렬)
    @Query("SELECT b FROM KDTBoardEntity b " +
            "WHERE b.kdtBoardCategory = :category " +
            "AND b.kdtSessionEntity.kdtSessionId = :sessionId " +
            "ORDER BY b.kdtBoardUpdatedAt DESC") // 최신 업데이트 순으로 정렬
    Page<KDTBoardEntity> findByKdtBoardCategoryAndSessionId(
            @Param("category") KDTBoardCategory category,
            @Param("sessionId") Long sessionId,
            Pageable pageable);

    // 특정 게시글을 가져오기 (최신 업데이트 순으로 정렬)
    @Query("SELECT b FROM KDTBoardEntity b " +
            "LEFT JOIN FETCH b.userEntity u " +
            "LEFT JOIN FETCH b.files f " + // 파일 연관 관계 추가
            "WHERE b.kdtSessionEntity.kdtSessionId = :sessionId " +
            "AND b.kdtBoardId = :boardId " +
            "AND b.kdtBoardCategory = :category")
    Optional<KDTBoardEntity> findBySessionIdAndKdtBoardIdAndBoardCategory(
            @Param("sessionId") Long sessionId,
            @Param("boardId") Long boardId,
            @Param("category") KDTBoardCategory category);

    // 카테고리, 제목, sessionId로 필터링 (최신 업데이트 순으로 정렬)
    @Query("SELECT b FROM KDTBoardEntity b " +
            "WHERE b.kdtBoardCategory = :category " +
            "AND b.kdtSessionEntity.kdtSessionId = :sessionId " +
            "AND b.kdtBoardTitle LIKE %:searchValue% " +
            "ORDER BY b.kdtBoardUpdatedAt DESC") // 최신 업데이트 순으로 정렬
    Page<KDTBoardEntity> findByKdtBoardCategoryAndKdtBoardTitleContainingAndSessionId(
            @Param("category") KDTBoardCategory category,
            @Param("searchValue") String searchValue,
            @Param("sessionId") Long sessionId,
            Pageable pageable);

    // 카테고리, 작성자 이름, sessionId로 필터링 (최신 업데이트 순으로 정렬)
    @Query("SELECT b FROM KDTBoardEntity b " +
            "WHERE b.kdtBoardCategory = :category " +
            "AND b.kdtSessionEntity.kdtSessionId = :sessionId " +
            "AND b.userEntity.name LIKE %:searchValue% " +
            "ORDER BY b.kdtBoardUpdatedAt DESC") // 최신 업데이트 순으로 정렬
    Page<KDTBoardEntity> findByKdtBoardCategoryAndUserEntityNameContainingAndSessionId(
            @Param("category") KDTBoardCategory category,
            @Param("searchValue") String searchValue,
            @Param("sessionId") Long sessionId,
            Pageable pageable);
}
