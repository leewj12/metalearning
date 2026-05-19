package com.Meta_learning.KDT.KDTentity.KDTAttEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KDTAttStatus {
    ARRIVAL("입실"),
    DEPARTURE("출석"),    // 퇴실을 출석으로 변경
    OUTGOING("외출"),
    EARLY_LEAVE("조퇴"),
    VACATION("휴가"),
    ABSENT("결석"),
    // TARDY("지각"), //지각 삭제
    SICK_LEAVE("병결"),
    ERROR("오류");        // 오류 추가

    private final String text;
}
