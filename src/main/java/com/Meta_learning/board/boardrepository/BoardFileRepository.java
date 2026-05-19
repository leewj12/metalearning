package com.Meta_learning.board.boardrepository;

import com.Meta_learning.board.boardentity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFileEntity, Long> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 정의
}