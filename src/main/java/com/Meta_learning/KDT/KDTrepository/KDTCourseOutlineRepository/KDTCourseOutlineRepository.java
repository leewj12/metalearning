package com.Meta_learning.KDT.KDTrepository.KDTCourseOutlineRepository;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTCourseOutlineRepository extends JpaRepository<KDTCourseOutlineEntity,Long> {
    List<KDTCourseOutlineEntity> findByKdtSessionEntityKdtSessionId(Long kdtSessionId);
    KDTCourseOutlineEntity findByKdtCourseOutlineId(Long kdtCourseOutlineId);

}
