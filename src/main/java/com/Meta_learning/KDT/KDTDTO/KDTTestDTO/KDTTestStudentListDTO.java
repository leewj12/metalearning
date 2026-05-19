package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestStudentListDTO {
    private Long kdtTestId;
    private String kdtTestTitle;
    private String authorName;              // 출제자 이름
    private Long authorUserId;              // 출제자 userId
    private String kdtTestCreatedAt; // 출제일
    private String kdtTestStartDate; // 시험 시작 시간
    private String kdtTestEndDate;   // 시험 마감 시간
    private boolean status;                     // 시험 만료
    private boolean available;                  // 시험 응시 가능

    private String kdtTestSubmitLastDate;       // 최종일
    private int actualScore;                   // 실제 점수
    private int maxScore;                 // 만점 기준
    private double percentile;              // 백분위


}
