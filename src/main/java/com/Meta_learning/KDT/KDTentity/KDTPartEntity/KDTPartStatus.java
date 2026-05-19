package com.Meta_learning.KDT.KDTentity.KDTPartEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KDTPartStatus {
    WAITING("대기"),
    IN_PROGRESS("수료중"),
    DISMISSED("제적"),
    COMPLETED("수료완료");

    private final String text;

    // 문자열을 Enum으로 변환 (valueOf 사용)
    public static KDTPartStatus fromString(String status) {
        try {
            return KDTPartStatus.valueOf(status);  // String을 Enum으로 변환
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    // text 값을 반환하는 메서드 추가
    public String getText() {
        return this.text;
    }
}
