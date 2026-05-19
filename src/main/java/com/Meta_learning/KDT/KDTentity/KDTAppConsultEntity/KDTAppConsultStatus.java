package com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KDTAppConsultStatus {
    PENDING("상담 대기중"),  // 상담 대기중
    COMPLETED("상담 완료"),  // 상담 완료
    CANCELLED("상담 취소");  // 상담 취소

    private final String text;  // 상태에 대한 설명

}
