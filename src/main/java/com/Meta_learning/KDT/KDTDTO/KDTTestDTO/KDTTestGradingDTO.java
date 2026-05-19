package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestGradingDTO {
    private Long kdtTestGradingId;
    private Long kdtTestSubmitId;  // 시험 답안 id
    private Long userId;  // 채점자
    private int kdtTestGradingScore;
    private LocalDateTime kdtTestGradingCreatedAt;
    private LocalDateTime kdtTestGradingUpdatedAt;
    private String kdtTestGradingComment;
}
