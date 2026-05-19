package com.Meta_learning.KDT.KDTentity.KDTBoardEntity;

import lombok.Getter;

@Getter
public enum KDTBoardCategory {
    NOTICE("공지사항"),
    POST("게시글"),
    QNA("QnA"),
    REVIEW("수강생 후기"),
    MATERIAL("자료실");  // 자료실 추가

    private final String description;

    // 생성자
    KDTBoardCategory(String description) {
        this.description = description;
    }
}
