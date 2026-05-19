package com.Meta_learning.board.boardentity;

import lombok.Getter;

@Getter
public enum BoardCategory {
    NOTICE("공지사항"),
    POST("게시글"),
    QNA("QnA"),
    REVIEW("수강생 후기");

    private final String description;

    // 생성자
    BoardCategory(String description) {
        this.description = description;
    }
}
