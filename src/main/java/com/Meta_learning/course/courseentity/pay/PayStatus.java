package com.Meta_learning.course.courseentity.pay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayStatus {
    PAY("결제 완료"),
    CANCEL("결제 취소"),
    PART_CANCEL("부분 취소");

    private final String text;
}
