package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseDescriptEntity;
import com.Meta_learning.course.courseentity.CourseDescriptFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseDescriptFileRepository extends JpaRepository<CourseDescriptFileEntity, Long> {
    List<CourseDescriptFileEntity> findByCourseDescript(CourseDescriptEntity courseDescript);
}
