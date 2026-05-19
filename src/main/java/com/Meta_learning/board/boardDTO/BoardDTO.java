package com.Meta_learning.board.boardDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BoardDTO {

    // 게시글 ID
    private Long boardId;

    // 유저 ID
    private Long userId;

    // 게시글 제목
    private String boardTitle;

    // 게시글 내용
    private String boardContent;

    // 게시글 생성일
    private LocalDateTime boardCreatedAt;

    // 게시글 수정일
    private LocalDateTime boardUpdatedAt;

    // 게시글 조회수
    private Long boardView;

    // 게시글 카테고리
    private String boardCategory;

    // 게시글 숨김 여부
    private Boolean boardHidden;

    // 게시글 답변 여부
    private Boolean boardAnswer;

}
