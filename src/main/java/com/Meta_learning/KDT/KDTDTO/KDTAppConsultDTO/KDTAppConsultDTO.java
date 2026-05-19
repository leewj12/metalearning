package com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO;


import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsult;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTAppConsultDTO {
    private Long kdtAppConsultId;  // 상담 ID
    private Long kdtSessionId;  // 세션 ID (참조하는 KDTSessionEntity의 ID)
    private String kdtAppConsultName;  // 신청자 이름
    private String kdtAppConsultGender;
    private LocalDate kdtAppConsultBirth;  // 생년월일
    private String kdtAppConsultPhone;  // 신청자 전화번호
    private String kdtAppConsultEmail;  // 신청자 이메일
    private String kdtAppConsultMotiv;  // 동기 (optional)
    private String kdtAppConsultCard;  // 국민내일배움카드 여부
    private String kdtAppConsultAppPath;  // 신청 경로
    private String kdtAppConsultEduLevel;  // 학력 수준
    private Boolean kdtAppConsultPrivacyAgree;  // 개인정보 동의 여부
    private Boolean kdtAppConsultMarketingAgree;  // 마케팅 동의 여부 (기본값 False)
    private String kdtAppConsultStatus;  // 상담 상태 (String 타입으로 변경)
    private LocalDateTime kdtAppCreatedAt;  // 작성일

    // 엔티티를 DTO로 변환하는 메서드
    public static KDTAppConsultDTO convertEntityToDTO(KDTAppConsult entity) {
        return KDTAppConsultDTO.builder()
                .kdtAppConsultId(entity.getKdtAppConsultId())
                .kdtSessionId(entity.getKdtSessionEntity().getKdtSessionId()) // 세션 ID는 KDTSessionEntity에서 가져옴
                .kdtAppConsultName(entity.getKdtAppConsultName())
                .kdtAppConsultGender(entity.getKdtAppConsultGender())
                .kdtAppConsultBirth(entity.getKdtAppConsultBirth())
                .kdtAppConsultPhone(entity.getKdtAppConsultPhone())
                .kdtAppConsultEmail(entity.getKdtAppConsultEmail())
                .kdtAppConsultMotiv(entity.getKdtAppConsultMotiv())
                .kdtAppConsultCard(entity.getKdtAppConsultCard())
                .kdtAppConsultAppPath(entity.getKdtAppConsultAppPath())
                .kdtAppConsultEduLevel(entity.getKdtAppConsultEduLevel())
                .kdtAppConsultPrivacyAgree(entity.getKdtAppConsultPrivacyAgree())
                .kdtAppConsultMarketingAgree(entity.getKdtAppConsultMarketingAgree())
                .kdtAppConsultStatus(entity.getKdtAppConsultStatus().toString())  // Enum을 String으로 변환
                .kdtAppCreatedAt(entity.getKdtAppCreatedAt())
                .build();
    }
}
