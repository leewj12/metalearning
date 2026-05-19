package com.Meta_learning.board.boardDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BoardTitleDTO {

    private Long boardId;  // 게시글 ID
    private String boardTitle;
    private String name;  // 작성자 이름
    private Long boardView;  // 게시글 조회수
    private LocalDateTime boardCreatedAt;  // 게시글 생성일
    private LocalDateTime boardUpdatedAt;  // 게시글 수정일
}
