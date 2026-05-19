package com.Meta_learning.course.courseentity.order;

import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // 주문 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 주문 상태 (yes, no)

    @CreatedDate
    @Column(name = "order_created_at", nullable = false, updatable = false)
    private LocalDateTime orderCreatedAt; // 주문 생성 시간

    @Column(name = "order_total_price", nullable = false)
    private Long orderTotalPrice; // 주문 총 가격

    @Column(name = "order_cancel_msg", nullable = true)
    private String orderCancelMsg; // 주문 취소 메시지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity; // 유저 엔티티 참조 (유저 ID)

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetails = new ArrayList<>(); // 주문 상세 리스트

    public void addOrderDetail(OrderDetailEntity orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }
}
