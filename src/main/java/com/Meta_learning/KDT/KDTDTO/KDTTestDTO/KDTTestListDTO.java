package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestListDTO {
    private Long kdtTestId;
    private String kdtTestTitle;
    private Long userId;
    private String authorName;
    private LocalDateTime kdtTestCreatedAt; // 출제일
    private LocalDateTime kdtTestStartDate; // 시험 시작 시간
    private LocalDateTime kdtTestEndDate;   // 시험 마감 시간
    private int actualCnt;     // 실제 응시자 수
    private int totalCnt;   // 총 응시자 수

    private double stdDev; // 표준편차
}
