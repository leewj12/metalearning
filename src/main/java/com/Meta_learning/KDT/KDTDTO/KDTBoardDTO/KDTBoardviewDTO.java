package com.Meta_learning.KDT.KDTDTO.KDTBoardDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTBoardviewDTO {

    private Long kdtBoardId;  // 게시글 ID
    private String kdtBoardTitle;
    private String kdtBoardContent;  // 게시글 내용
    private LocalDateTime kdtBoardCreatedAt;  // 게시글 생성일
    private LocalDateTime kdtBoardUpdatedAt;  // 게시글 수정일
    private Long kdtBoardView;  // 게시글 조회수
    private String name;  // 작성자 이름

    // 여러 파일 이름 리스트
    private List<String> kdtFileNams;  // 여러 파일 이름

    // 여러 파일 UUID 리스트
    private List<String> kdtfileUUIDs;  // 여러 파일 UUID
}
