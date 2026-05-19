package com.Meta_learning.course.courseentity.pay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayDetailStatus {
    PAY("결제 완료"),
    CANCEL("결제 취소");

    private final String text;
}
