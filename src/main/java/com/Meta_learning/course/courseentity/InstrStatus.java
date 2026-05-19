package com.Meta_learning.course.courseentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstrStatus {
    PENDING("대기"),
    CANCELED("취소"),
    APPROVED("승인");

    private final String text;
}