package com.Meta_learning.course.courserepository.cart;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.cart.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    void deleteAllByCourse(CourseEntity course);
}
