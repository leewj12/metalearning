package com.Meta_learning.KDT.KDTDTO.KDTConsultDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTConsultDTO {
    private Long kdtConsultId;
    private Long consultOrder;
    private Long userId;            // 국비 담당자 user_id
    private String authorName;      // 국비 담당자명
    private Long kdtPartId;         // 국비 참가자 id
    private String kdtPartName;     // 참가자명
    private String kdtConsultTitle; // 상담 제목
    private String kdtConsultContent;   // 상담 내용
    private String kdtConsultCategory;  // 상담 카테고리 (예: 진로, 고민, 건의)
    private LocalDate kdtConsultDate;   // 상담 날짜
}
