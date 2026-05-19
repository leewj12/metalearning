package com.Meta_learning.KDT.KDTrepository.KDTCourseRepository;


import com.Meta_learning.KDT.KDTentity.KDTCourseEntity.KDTCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KDTCourseRepository extends JpaRepository <KDTCourseEntity,Long>{

    boolean existsByKdtCourseTitle(String kdtCourseTitle);

}
