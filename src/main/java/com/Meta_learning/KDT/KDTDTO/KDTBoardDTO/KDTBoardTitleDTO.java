package com.Meta_learning.KDT.KDTDTO.KDTBoardDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTBoardTitleDTO {

    private Long kdtBoardId;  // 게시글 ID
    private String kdtBoardTitle;  // 게시글 제목
    private String name;  // 작성자 이름
    private Long kdtBoardView;  // 게시글 조회수 (kdtBoardViewCount와 일치하도록)
    private String kdtBoardCategory; // 게시판 카테고리 (예: 질문, 답변 등) - 열거형을 String으로 처리
    private LocalDateTime kdtBoardCreatedAt;  // 게시글 생성일
    private LocalDateTime kdtBoardUpdatedAt;  // 게시글 수정일

    // 날짜 포맷을 지정하여 변환하는 메소드 (년월일 오전/오후 시:분 형식)
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh시 mm분"); // 년월일 오전/오후 시:분 형식
        return kdtBoardCreatedAt != null ? kdtBoardCreatedAt.format(formatter) : null;
    }

    public String getFormattedUpdatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh시 mm분"); // 년월일 오전/오후 시:분 형식
        return kdtBoardUpdatedAt != null ? kdtBoardUpdatedAt.format(formatter) : null;
    }

}
