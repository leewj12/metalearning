package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseVideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseVideoRepository extends JpaRepository<CourseVideoEntity, Long> {
    List<CourseVideoEntity> findByCourseDetailIn(List<CourseDetailEntity> courseDetails);

    // courseDetailId로 관련된 CourseVideoEntity 조회
    Optional<CourseVideoEntity> findByCourseDetail_CourseDetailId(Long courseDetailId);

}
