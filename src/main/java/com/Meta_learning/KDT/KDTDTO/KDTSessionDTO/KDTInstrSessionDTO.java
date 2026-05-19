package com.Meta_learning.KDT.KDTDTO.KDTSessionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KDTInstrSessionDTO {
    private Long kdtSessionId;  // 회차 id
    private Long kdtCourseId;   // 교육 id
    private int kdtSessionNum;  // 회차 정보
    private String kdtSessionTitle;     // 회차제목
    private String kdtCourseTitle;      // 교육 제목
    private LocalDate kdtSessionStartDate;  // 시작일
    private LocalDate kdtSessionEndDate;    // 종료일
    private String kdtSessionCategory;      // 카테고리
    private Boolean kdtSessionOnline;       // 온라인 여부
    private String kdtSessionStatus;        // 상태
}
