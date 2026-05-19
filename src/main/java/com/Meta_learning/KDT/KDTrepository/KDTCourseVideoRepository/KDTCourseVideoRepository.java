package com.Meta_learning.KDT.KDTrepository.KDTCourseVideoRepository;


import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTCourseVideoRepository extends JpaRepository<KDTCourseVideoEntity,Long> {

    List<KDTCourseVideoEntity>  findByKdtCourseOutlineEntityKdtCourseOutlineId(Long kdtCourseOutlineId);
    boolean existsByKdtCourseOutlineEntity_KdtCourseOutlineId(Long kdtCourseOutlineId);

}
