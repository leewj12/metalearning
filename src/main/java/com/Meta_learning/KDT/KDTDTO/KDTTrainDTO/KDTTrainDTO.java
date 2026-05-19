package com.Meta_learning.KDT.KDTDTO.KDTTrainDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KDTTrainDTO {
    private Long kdtTrainId;             // 훈련 ID
    private Long userId;                 // 작성자 ID
    private String name;                   // 작성자 이름
    private Long kdtSessionId;           // 국비회차 ID
    private Long kdtStaffId;             // 강사 ID
    private String kdtTrainTitle;        // 훈련일지 제목
    private LocalDate kdtTrainDate;      // 훈련 날짜
    private String kdtTrainContent;      // 훈련일지 내용
    private String kdtTrainSubject;      // 훈련일지 과목
}