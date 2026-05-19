package com.Meta_learning.course.courseservice.order;

import com.Meta_learning.course.courseentity.order.OrderEntity;
import com.Meta_learning.course.courserepository.order.OrderRepository;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderEntity> findOrdersByUser(UserEntity user) {
        return orderRepository.findByUserEntity(user);
    }
}
