package com.Meta_learning.course.courseentity.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    COMPLETED("결제 완료"),
    PENDING("결제 대기");

    private final String text;
}