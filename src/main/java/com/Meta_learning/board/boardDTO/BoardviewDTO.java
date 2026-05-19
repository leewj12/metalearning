package com.Meta_learning.board.boardDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BoardviewDTO {

    private Long boardId;  // 게시글 ID
    private String boardTitle;
    private String boardContent;  // 게시글 내용
    private LocalDateTime boardCreatedAt;  // 게시글 생성일
    private LocalDateTime boardUpdatedAt;  // 게시글 수정일
    private Long boardView;  // 게시글 조회수
    private String boardCategory;  // 게시글 카테고리
    private String name;  // 작성자 이름

    // 여러 파일 이름 리스트
    private List<String> fileNames;  // 여러 파일 이름

    // 여러 파일 UUID 리스트
    private List<String> fileUUIDs;  // 여러 파일 UUID

    // 여러 파일 타입 리스트
    private List<String> fileTypes;  // 여러 파일 타입
}
