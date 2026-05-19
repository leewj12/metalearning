package com.Meta_learning.KDT.KDTrepository.KDTSessionEvalRepository;

import com.Meta_learning.KDT.KDTentity.KDTSessionEvalEntity.KDTSessionEvalEntity;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;  // KDTPartEntity import 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;  // Optional import 추가

@Repository
public interface KDTSessionEvalRepository extends JpaRepository<KDTSessionEvalEntity, Long> {
    // KDTPartEntity로 이미 존재하는 리뷰를 찾는 메서드
    Optional<KDTSessionEvalEntity> findByKdtPartEntity(KDTPartEntity kdtPartEntity);
}
