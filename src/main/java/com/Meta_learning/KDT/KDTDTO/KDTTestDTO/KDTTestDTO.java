package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestDTO {
    private Long kdtTestId;
    private Long kdtSessionId;  // 국비회차id
    private Long userId;  // 국비 참가 담당자 user_id()
    private String kdtTestTitle;
    private LocalDateTime kdtTestStartDate;
    private LocalDateTime kdtTestEndDate;
    private LocalDateTime kdtTestCreatedAt;
    private LocalDateTime kdtTestUpdatedAt;
}
