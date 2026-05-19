package com.Meta_learning.course.courseentity;


import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instr")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
//@EntityListeners(AuditingEntityListener.class)
public class InstrEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instr_id") // 강사 ID
    private Long instrId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity; // 유저 엔티티 참조 (유저 ID)

    @Column(name = "instr_descript") // 강사 설명
    private String instrDescript;

    @Column(name = "instr_company") // 강사 소속 회사
    private String instrCompany;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "instr_status", nullable = false) // 강사 상태
    private InstrStatus instrStatus = InstrStatus.PENDING; // 기본값 설정

/*    @CreatedDate
    @Column(name = "created_at", updatable = false) // 생성 날짜
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at") // 수정 날짜
    private LocalDateTime updatedAt;*/

    // 업데이트 메서드
    public void update(String instrDescript, String instrCompany, InstrStatus instrStatus) {
        if (instrDescript != null) {
            this.instrDescript = instrDescript;
        }
        if (instrCompany != null) {
            this.instrCompany = instrCompany;
        }
        if (instrStatus != null) {
            this.instrStatus = instrStatus;
        }
//        this.updatedAt = LocalDateTime.now(); // 항상 현재 시간으로 수정일 갱신
    }

    public void updateInstrStatus(InstrStatus instrStatus) {
        this.instrStatus = instrStatus;
    }
}