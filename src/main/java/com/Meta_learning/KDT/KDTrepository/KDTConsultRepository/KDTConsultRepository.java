package com.Meta_learning.KDT.KDTrepository.KDTConsultRepository;


import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KDTConsultRepository extends JpaRepository<KDTConsultEntity,Long> {
    KDTConsultEntity findByKdtConsultId(Long kdtConsultId);

    // KDTSessionId에 따른 모든 상담일지를 날짜 내림차순으로 조회하는 메서드
    Page<KDTConsultEntity> findByKdtPartEntity_KdtSessionEntity_KdtSessionIdOrderByKdtConsultDateDesc(Long kdtSessionId, Pageable pageable);

    // 검색 조건에 맞는 상담일지를 날짜 내림차순으로 조회하는 메서드
    @Query("SELECT c FROM KDTConsultEntity c " +
            "WHERE c.kdtPartEntity.kdtSessionEntity.kdtSessionId = :kdtSessionId " +
            "AND (:searchCategory IS NULL OR " +
            "( :searchCategory = 'category' AND c.kdtConsultCategory LIKE %:search% ) OR " +
            "( :searchCategory = 'staffName' AND c.userEntity.name LIKE %:search% ) OR " +
            "( :searchCategory = 'studentName' AND c.kdtPartEntity.userEntity.name LIKE %:search% ) OR " +
            "( :searchCategory = 'title' AND c.kdtConsultTitle LIKE %:search% ) OR " +
            "( :searchCategory = 'content' AND c.kdtConsultContent LIKE %:search% ))")
    Page<KDTConsultEntity> searchWithPaging(Long kdtSessionId, String searchCategory, String search, Pageable pageable);
}
