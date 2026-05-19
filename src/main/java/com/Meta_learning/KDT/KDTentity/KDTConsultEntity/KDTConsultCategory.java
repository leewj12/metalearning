package com.Meta_learning.KDT.KDTentity.KDTConsultEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KDTConsultCategory {
    CAREER("진로"),
    CONCERN("고민"),
    SUGGESTION("건의");

    private final String text;

    // text에 해당하는 KDTConsultCategory를 반환하는 메서드
    public static KDTConsultCategory getCategoryByText(String searchCategory) {
        for (KDTConsultCategory category : KDTConsultCategory.values()) {
            if (category.getText().contains(searchCategory)) {  // 부분 일치 처리
                return category;
            }
        }
        return null;  // 일치하는 카테고리가 없는 경우
    }
}
