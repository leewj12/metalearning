package com.Meta_learning.course.coursecontroller.dto.response;

import com.Meta_learning.course.courseentity.pay.PayStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class PayListResponse {
    private Long payId; // 결제 ID
    private Long orderId; // 유저 엔티티 참조 (유저 ID)
    private String payPayer; // 결제자
    private String payPg; // PG사
    private String payMethod; // 결제 수단
    private LocalDateTime payCreatedAt; // 결제 생성 시간
    private Long payTotalPrice; // 결제 총 가격
    private PayStatus payStatus; // 결제 상태 (pay, cancel, part_cancel)
}
