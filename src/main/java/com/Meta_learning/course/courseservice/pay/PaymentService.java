package com.Meta_learning.course.courseservice.pay;

import com.Meta_learning.course.coursecontroller.dto.request.PaymentItemDto;
import com.Meta_learning.course.coursecontroller.dto.request.PaymentRequest;
import com.Meta_learning.course.coursecontroller.dto.response.PayListResponse;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.order.OrderDetailEntity;
import com.Meta_learning.course.courseentity.order.OrderEntity;
import com.Meta_learning.course.courseentity.order.OrderStatus;
import com.Meta_learning.course.courseentity.pay.PayDetailEntity;
import com.Meta_learning.course.courseentity.pay.PayDetailStatus;
import com.Meta_learning.course.courseentity.pay.PayEntity;
import com.Meta_learning.course.courseentity.pay.PayStatus;
import com.Meta_learning.course.courserepository.CourseRepository;
import com.Meta_learning.course.courserepository.order.OrderDetailRepository;
import com.Meta_learning.course.courserepository.order.OrderRepository;
import com.Meta_learning.course.courserepository.pay.PayRepository;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PayRepository payRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void processPayment(PaymentRequest paymentRequest) {
        // 1. OrderEntity 생성 및 저장
        OrderEntity order = OrderEntity.builder()
                .userEntity(findUserById(paymentRequest.getUserId()))
                .orderTotalPrice(paymentRequest.getTotalPrice())
                .orderStatus(OrderStatus.COMPLETED)
                .build();

        // 먼저 OrderEntity 저장 (ID 생성)
        OrderEntity savedOrder = orderRepository.save(order);

        // 2. OrderDetailEntity 생성 및 추가
        paymentRequest.getItems().forEach(item -> {
            OrderDetailEntity orderDetail = OrderDetailEntity.builder()
                    .course(findCourseById(item.getCourseId()))
                    .orderDetailPrice(item.getCoursePrice())
                    .order(savedOrder)
                    .build();
            savedOrder.addOrderDetail(orderDetail);
        });

        // 변경 사항 저장 (OrderDetailEntity도 저장됨)
        orderRepository.save(savedOrder);

        // 2. PayEntity 생성 및 저장
        PayEntity pay = PayEntity.builder()
                .order(order)
                .payPg(paymentRequest.getRsp().get("pg_provider").toString())
                .payMethod(paymentRequest.getRsp().get("pay_method").toString())
                .payTotalPrice(paymentRequest.getTotalPrice())
                .payPayer(paymentRequest.getRsp().get("buyer_name").toString())
                .payStatus(PayStatus.PAY)
                .build();

        // 3. PayDetailEntity 생성 및 PayEntity와 연결
        order.getOrderDetails().forEach(orderDetail -> {
            PayDetailEntity payDetail = PayDetailEntity.builder()
                    .orderDetail(orderDetail)
                    .payDetailPrice(orderDetail.getOrderDetailPrice())
                    .payDetailStatus(PayDetailStatus.PAY)
                    .pay(pay) // PayEntity와 연결
                    .build();
            pay.getPayDetails().add(payDetail); // PayEntity에 PayDetail 추가
        });

        // 4. PayEntity 저장 (PayDetail도 함께 저장)
        payRepository.save(pay);



    }

    private UserEntity findUserById(Long userId) {
        // UserEntity 찾기 로직
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자(ID: " + userId + ")를 찾을 수 없습니다.");
        }
        return user;
    }

    private CourseEntity findCourseById(Long courseId) {
        // CourseEntity 찾기 로직
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의(ID: " + courseId + ")를 찾을 수 없습니다."));
    }

    public List<PayListResponse> getAllPayList() {
        // 모든 PayEntity 가져오기
        List<PayEntity> payEntities = payRepository.findAll();

        // PayEntity를 PayListResponse로 변환
        return payEntities.stream().map(pay -> PayListResponse.builder()
                .payId(pay.getPayId())
                .orderId(pay.getOrder().getOrderId())
                .payPayer(pay.getPayPayer())
                .payPg(pay.getPayPg())
                .payMethod(pay.getPayMethod())
                .payCreatedAt(pay.getPayCreatedAt())
                .payTotalPrice(pay.getPayTotalPrice())
                .payStatus(pay.getPayStatus())
                .build()
        ).toList(); // Java 16+의 toList() 사용
    }

    @Transactional
    public void refundPayment(Long payId) {
        // 1. 결제 정보 조회
        PayEntity payEntity = payRepository.findById(payId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다."));

        // 2. 결제 상태를 환불로 업데이트
        if (payEntity.getPayStatus() == PayStatus.PAY) {
            payEntity.setPayStatus(PayStatus.CANCEL); // 전체 취소
        }

        // 4. PG사와 통신하여 실제 환불 처리 (선택)
        // refundWithPG(payEntity);
    }

//    // 선택적으로 PG사 통신을 처리하는 메서드 추가
//    private void refundWithPG(PayEntity payEntity) {
//        // TODO: PG사와 통신하여 환불 처리 구현
//        // 이 부분은 사용 중인 PG사 API에 따라 구현이 달라집니다.
//    }
}
