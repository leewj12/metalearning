package com.Meta_learning.course.courseentity.pay;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.order.OrderDetailEntity;
import com.Meta_learning.course.courseentity.order.OrderEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "pay_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PayDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_detail_id")
    private Long payDetailId; // 결제 상세 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", referencedColumnName = "order_detail_id", nullable = false)
    private OrderDetailEntity orderDetail; // 주문 상세 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_id", referencedColumnName = "pay_id", nullable = false)
    private PayEntity pay; // 결제 참조

    @Column(name = "pay_detail_price", nullable = false)
    private Long payDetailPrice; // 결제 상세 가격

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_detail_status", nullable = false)
    private PayDetailStatus payDetailStatus; // 결제 상세 상태 (pay, cancel)
}
