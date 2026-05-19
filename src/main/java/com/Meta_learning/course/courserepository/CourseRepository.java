package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.CourseStatus;
import com.Meta_learning.course.courseentity.InstrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByInstr(InstrEntity instr);

    // 승인된 강의 조회
    List<CourseEntity> findByCourseStatus(CourseStatus status);

    // 강사가 등록한 강의 수 조회
    Long countByInstr(InstrEntity instr);

    @Query("SELECT YEAR(c.courseCreatedAt) AS year, MONTH(c.courseCreatedAt) AS month, COUNT(c) AS courseCount " +
            "FROM CourseEntity c " +
            "GROUP BY YEAR(c.courseCreatedAt), MONTH(c.courseCreatedAt) " +
            "ORDER BY year, month")
    List<Object[]> countCoursesByMonth();
}
