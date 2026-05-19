package com.Meta_learning.board.boardrepository;

import com.Meta_learning.board.boardentity.BoardCategory;
import com.Meta_learning.board.boardentity.BoardEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 정의
    // 카테고리가 'REVIEW'인 게시글들과 관련된 파일까지 함께 조회
    @Query("SELECT b FROM BoardEntity b " +
            "LEFT JOIN FETCH b.boardFiles " +  // BoardEntity와 관련된 BoardFileEntity들을 함께 가져옴
            "LEFT JOIN FETCH b.userEntity " +   // BoardEntity와 관련된 UserEntity를 함께 가져옴
            "WHERE b.boardCategory = :category")
    List<BoardEntity> findByBoardCategoryWithFiles(BoardCategory category);


    @Query(value = "SELECT DISTINCT b FROM BoardEntity b " +
            "LEFT JOIN FETCH b.boardFiles " +
            "LEFT JOIN FETCH b.userEntity " +
            "WHERE b.boardCategory = :category",
            countQuery = "SELECT COUNT(b) FROM BoardEntity b WHERE b.boardCategory = :category")
    Page<BoardEntity> findByBoardCategoryWithFilesAndUser(@Param("category") BoardCategory category, Pageable pageable);




}