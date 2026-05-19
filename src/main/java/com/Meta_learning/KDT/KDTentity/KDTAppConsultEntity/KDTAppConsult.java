package com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity;

import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "KDT_app_consult")
public class KDTAppConsult {

    // 기본 키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_app_consult_id")
    private Long kdtAppConsultId;

    // 세션 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_session_id", referencedColumnName = "KDT_session_id", nullable = false)
    private KDTSessionEntity kdtSessionEntity;  // KDTSessionEntity 참조

    // 신청자 이름
    @Column(name = "KDT_app_consult_name", nullable = false)
    private String kdtAppConsultName;

    //신청자 성별
    @Column(name = "KDT_app_consult_gender", nullable = false)
    private String kdtAppConsultGender;

    // 생년월일 (DATE 타입으로 저장)
    @Column(name = "KDT_app_consult_birth", nullable = false)
    private LocalDate kdtAppConsultBirth;

    // 신청자 전화번호
    @Column(name = "KDT_app_consult_phone", nullable = false)
    private String kdtAppConsultPhone;

    // 신청자 이메일
    @Column(name = "KDT_app_consult_email", nullable = false)
    private String kdtAppConsultEmail;

    // 동기 (optional)
    @Column(name = "KDT_app_consult_motiv")
    private String kdtAppConsultMotiv;

    // 카드 여부 (국민내일배움카드 여부)
    @Column(name = "KDT_app_consult_card", nullable = false)
    private String kdtAppConsultCard;

    // 신청 경로
    @Column(name = "KDT_app_consult_app_path", nullable = false)
    private String kdtAppConsultAppPath;

    // 학력 수준
    @Column(name = "KDT_app_consult_edu_level", nullable = false)
    private String kdtAppConsultEduLevel;

    // 개인정보 동의 여부
    @Column(name = "KDT_app_consult_privacy_agree", nullable = false)
    private Boolean kdtAppConsultPrivacyAgree;

    // 마케팅 동의 여부 (기본값 False)
    @Builder.Default
    @Column(name = "KDT_app_consult_marketing_agree", nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean kdtAppConsultMarketingAgree = false;

    // 처리 상태 (Enum으로 변경)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "KDT_app_consult_status", nullable = false)
    private KDTAppConsultStatus kdtAppConsultStatus = KDTAppConsultStatus.PENDING; // 기본값: 상담 대기중

    @CreatedDate
    @Column(name = "KDT_app_created_at", updatable = false)
    private LocalDateTime kdtAppCreatedAt;  // 사용자 계정 생성일자

    // 상태를 변경하는 메서드 (KDTAppConsultStatus 사용)
    public void setStatus(KDTAppConsultStatus status) {
        this.kdtAppConsultStatus = status;
    }
    // 업데이트 메서드
    public void update(KDTAppConsultDTO kdtAppConsultDTO) {
        if (kdtAppConsultDTO.getKdtAppConsultName() != null) {
            this.kdtAppConsultName = kdtAppConsultDTO.getKdtAppConsultName();
        }
        if (kdtAppConsultDTO.getKdtAppConsultGender() != null) {
            this.kdtAppConsultGender = kdtAppConsultDTO.getKdtAppConsultGender();
        }
        if (kdtAppConsultDTO.getKdtAppConsultBirth() != null) {
            this.kdtAppConsultBirth = kdtAppConsultDTO.getKdtAppConsultBirth();
        }
        if (kdtAppConsultDTO.getKdtAppConsultPhone() != null) {
            this.kdtAppConsultPhone = kdtAppConsultDTO.getKdtAppConsultPhone();
        }
        if (kdtAppConsultDTO.getKdtAppConsultEmail() != null) {
            this.kdtAppConsultEmail = kdtAppConsultDTO.getKdtAppConsultEmail();
        }
        if (kdtAppConsultDTO.getKdtAppConsultMotiv() != null) {
            this.kdtAppConsultMotiv = kdtAppConsultDTO.getKdtAppConsultMotiv();
        }
        if (kdtAppConsultDTO.getKdtAppConsultCard() != null) {
            this.kdtAppConsultCard = kdtAppConsultDTO.getKdtAppConsultCard();
        }
        if (kdtAppConsultDTO.getKdtAppConsultAppPath() != null) {
            this.kdtAppConsultAppPath = kdtAppConsultDTO.getKdtAppConsultAppPath();
        }
        if (kdtAppConsultDTO.getKdtAppConsultEduLevel() != null) {
            this.kdtAppConsultEduLevel = kdtAppConsultDTO.getKdtAppConsultEduLevel();
        }
        if (kdtAppConsultDTO.getKdtAppConsultPrivacyAgree() != null) {
            this.kdtAppConsultPrivacyAgree = kdtAppConsultDTO.getKdtAppConsultPrivacyAgree();
        }
        if (kdtAppConsultDTO.getKdtAppConsultMarketingAgree() != null) {
            this.kdtAppConsultMarketingAgree = kdtAppConsultDTO.getKdtAppConsultMarketingAgree();
        }

        // Enum 값 처리 (문자열을 Enum으로 변환)
        if (kdtAppConsultDTO.getKdtAppConsultStatus() != null) {
            try {
                this.kdtAppConsultStatus = KDTAppConsultStatus.valueOf(kdtAppConsultDTO.getKdtAppConsultStatus());
            } catch (IllegalArgumentException e) {
                // Enum 값이 잘못된 경우 처리
                System.err.println("Invalid KDTAppConsultStatus value: " + kdtAppConsultDTO.getKdtAppConsultStatus());
            }
        }
    }

}
