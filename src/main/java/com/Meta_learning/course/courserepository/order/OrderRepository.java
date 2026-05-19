package com.Meta_learning.course.courserepository.order;

import com.Meta_learning.course.courseentity.order.OrderEntity;
import com.Meta_learning.user.userentity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // 특정 사용자와 연관된 주문 조회
    List<OrderEntity> findByUserEntity(UserEntity userEntity);
}
