package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseDescriptEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseDescriptRepository extends JpaRepository<CourseDescriptEntity, Long> {
    Optional<CourseDescriptEntity> findByCourse(CourseEntity courseEntity);
}
