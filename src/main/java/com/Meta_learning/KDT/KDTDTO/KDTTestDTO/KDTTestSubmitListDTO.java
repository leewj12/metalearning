package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestSubmitListDTO {
    private Long kdtPartId;                 // 학생 참가자 번호
    private Long userId;                    // 학생 유저 id
    private String kdtPartName;             // 학생 이름
    private LocalDateTime kdtTestSubmitCreatedAt;   // 첫 제출일
    private LocalDateTime kdtTestSubmitUpdatedAt;   // 수정일
    private int actualScore;                   // 실제 점수
    private int maxScore;                 // 만점 기준
    private double percentile;              // 백분위

    // 시험 이름/ 학생 이름 /첫제출 / 수정날짜 / 점수 /

}
