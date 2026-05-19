package com.Meta_learning.KDT.KDTentity.KDTPartEntity;


import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_part")
@Getter
public class KDTPartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_part_id")
    private Long kdtPartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_session_id", referencedColumnName = "KDT_session_id", nullable = false)
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity;  // 국비 참가 담당자 user_id()

    @Enumerated(EnumType.STRING)
    @Column(name = "KDT_part_status", nullable = false)
    private KDTPartStatus kdtPartStatus;   // 대기, 수료중, 제적, 수료완료

    @Column(name = "KDT_part_emp", nullable = false)
    private Boolean kdtPartEmp;     // 취직 여부 False:구직상태 / True:취직상태


    // 하나의 메서드로 상태를 업데이트
    public void updateFields(KDTPartStatus newStatus, Boolean newEmploymentStatus) {
        // 새로운 상태 값이 null이 아니면 kdtPartStatus 업데이트
        if (newStatus != null) {
            this.kdtPartStatus = newStatus;
        }

        // 새로운 취업 상태 값이 null이 아니면 kdtPartEmp 업데이트
        if (newEmploymentStatus != null) {
            this.kdtPartEmp = newEmploymentStatus;
        }
    }

}
