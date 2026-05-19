package com.Meta_learning.KDT.KDTentity.KDTSessionEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KDTSessionStatus {
    WAITING("대기"),
    ONGOING("진행중"),
    FINISHED("종료");

    private final String text;
}
