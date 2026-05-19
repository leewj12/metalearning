package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseDetailRepository extends JpaRepository<CourseDetailEntity, Long> {
    // courseId를 기준으로 가장 큰 courseDetailOrder를 찾음
    Optional<CourseDetailEntity> findTopByCourse_CourseIdOrderByCourseDetailOrderDesc(Long courseId);

    List<CourseDetailEntity> findByCourse(CourseEntity courseEntity);

    // 특정 CourseEntity와 연관된 모든 세부 정보를 가져오는 메서드
    List<CourseDetailEntity> findAllByCourse(CourseEntity courseEntity);
    // Integer findTopByCourseCourseIdOrderByCourseDetailOrderDesc(Long courseId);


    // courseDetailId로 CourseDetailEntity 조회
    Optional<CourseDetailEntity> findByCourseDetailId(Long courseDetailId);

}
