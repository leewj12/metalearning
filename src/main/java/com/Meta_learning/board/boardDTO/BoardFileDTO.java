package com.Meta_learning.board.boardDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BoardFileDTO {

    // 파일 ID
    private Long fileId;

    // 게시글 ID (연결된 게시글의 ID)
    private Long boardId;

    // 파일 이름
    private String fileName;

    // 파일 UUID (파일 고유 식별자)
    private String fileUUID;

    // 파일 크기
    private Long fileSize;

    // 파일 타입 (예: image/jpeg, application/pdf 등)
    private String fileType;

    // 파일 업로드 시간
    private LocalDateTime fileTime;

    // 파일 목록을 담을 리스트
    @Builder.Default
    private List<BoardFileDTO> files = new ArrayList<>();

    // 파일 추가 메서드
    public void addFile(BoardFileDTO fileDTO) {
        this.files.add(fileDTO);
    }
}
