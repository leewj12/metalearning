package com.Meta_learning.course.courseentity.order;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "orders_detail",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_order_course",
                columnNames = {"order_id", "course_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId; // 주문 상세 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private CourseEntity course; // 코스 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private OrderEntity order; // 유저 엔티티 참조 (유저 ID)

    @Column(name = "order_detail_price", nullable = false)
    private Long orderDetailPrice; // 주문 상세 가격

    @Column(name = "order_detail_cancel_msg", nullable = true)
    private String orderDetailCancelMsg; // 주문 상세 취소 메시지

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
