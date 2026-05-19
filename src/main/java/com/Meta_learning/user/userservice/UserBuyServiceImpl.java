package com.Meta_learning.user.userservice;

import com.Meta_learning.course.courserepository.order.OrderDetailRepository;
import com.Meta_learning.course.courseentity.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBuyServiceImpl implements UserBuyService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public boolean hasUserPurchasedCourse(Long userId, Long courseId) {
        // 주문 상세에서 해당 유저가 해당 코스를 구매했고, 결제 완료 상태인지를 확인
        return orderDetailRepository.existsByOrder_UserEntity_UserIdAndCourse_CourseIdAndOrder_OrderStatus(userId, courseId, OrderStatus.COMPLETED);
    }
}
