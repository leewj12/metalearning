package com.Meta_learning.KDT.KDTDTO.KDTBoardDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTBoardDTO {

    private Long kdtBoardId; // 게시판 ID

    private Long kdtSessionId;  // KDTSessionEntity의 id와 일치

    private Long userId;  // UserEntity의 id와 일치

    private String kdtBoardTitle; // 게시판 제목

    private String kdtBoardContent; // 게시판 내용

    private LocalDateTime kdtBoardCreatedAt; // 게시판 생성일

    private LocalDateTime kdtBoardUpdatedAt; // 게시판 수정 일자

    private Long kdtBoardViewCount; // 게시판 조회수

    private String kdtBoardCategory; // 게시판 카테고리 (enum -> name() 메서드로 String 변환)

    private Boolean kdtBoardHidden; // 게시판 숨김 여부

    private Boolean kdtBoardAnswerCompleted; // 게시판 답변 완료 여부
}
