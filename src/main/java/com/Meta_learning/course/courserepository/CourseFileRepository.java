package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseFileRepository extends JpaRepository<CourseFileEntity, Long> {
    List<CourseFileEntity> findByCourseDetailIn(List<CourseDetailEntity> courseDetails);

    void deleteByCourseDetail_CourseDetailId(Long detailId);
}
