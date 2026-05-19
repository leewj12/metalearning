package com.Meta_learning.KDT.KDTrepository.KDTBoardrepository;

import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KDTBoardFileRepository extends JpaRepository<KDTBoardFileEntity, Long> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 정의
}