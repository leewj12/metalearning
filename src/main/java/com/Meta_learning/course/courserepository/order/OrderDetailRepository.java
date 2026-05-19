package com.Meta_learning.course.courserepository.order;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.order.OrderDetailEntity;
import com.Meta_learning.course.courseentity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    // courseId만으로 중복 여부 확인
    boolean existsByCourse_CourseId(Long courseId);

    boolean existsByOrder_UserEntity_UserIdAndCourse_CourseIdAndOrder_OrderStatus(Long userId, Long courseId, OrderStatus orderStatus);

    // 특정 강의가 주문된 적이 있는지 확인 (존재하면 삭제 불가능)
    boolean existsByCourse(CourseEntity course);

}



