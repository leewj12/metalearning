package com.Meta_learning.course.coursecontroller.dto.request;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long userId; // 사용자 ID
    private List<PaymentItemDto> items; // 결제 항목 리스트
    private Long totalPrice; // 총 결제 금액
    private Map<String, Object> rsp; // 아임포트 응답 정보
}
