package com.Meta_learning.course.courseentity.pay;

import com.Meta_learning.course.courseentity.order.OrderEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pay")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId; // 결제 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private OrderEntity order; // 유저 엔티티 참조 (유저 ID)

    @Column(name = "pay_pg", nullable = false)
    private String payPg; // PG사

    @Column(name = "pay_method", nullable = false)
    private String payMethod; // 결제 수단

    @CreatedDate
    @Column(name = "pay_created_at", nullable = false, updatable = false)
    private LocalDateTime payCreatedAt; // 결제 생성 시간

    @Column(name = "pay_total_price", nullable = false)
    private Long payTotalPrice; // 결제 총 가격

    @Column(name = "pay_payer", nullable = false)
    private String payPayer; // 결제자

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false)
    private PayStatus payStatus; // 결제 상태 (pay, cancel, part_cancel)

    @Builder.Default
    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayDetailEntity> payDetails = new ArrayList<>(); // 결제 상세 내역

    public void setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }
}
