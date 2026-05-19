package com.Meta_learning.KDT.KDTDTO.KDTBoardDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTBoardFileDTO {

    private Long kdtFileId; // 파일의 고유 ID

    private Long kdtBoardId; // 게시판 ID (연관된 게시판의 ID)

    private String kdtFileName; // 파일의 이름

    private String kdtFileUUID; // 파일의 UUID (중복 방지 및 고유 식별자)

    private Long kdtFileSize; // 파일의 크기 (바이트 단위)

    private String kdtFileType; // 파일의 타입 (예: image/jpeg, application/pdf 등)

    private LocalDateTime kdtFileTime; // 파일 업로드 시간
}
