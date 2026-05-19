package com.Meta_learning.KDT.KDTentity.KDTTestEntity;

import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestDTO;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_test")
@Getter
public class KDTTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_test_id")
    private Long kdtTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_session_id", referencedColumnName = "KDT_session_id", nullable = false)
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity;  // 국비 참가 담당자 user_id()

    @Column(name="KDT_test_title", nullable=false)
    private String kdtTestTitle;

    @Column(name = "KDT_test_start_date", nullable = false)
    private LocalDateTime kdtTestStartDate;

    @Column(name = "KDT_test_end_date", nullable = false)
    private LocalDateTime kdtTestEndDate;

    @Column(name = "KDT_test_created_at", nullable = false, updatable = false)
    private LocalDateTime kdtTestCreatedAt;

    @Column(name = "KDT_test_updated_at")
    private LocalDateTime kdtTestUpdatedAt;

    public void update(KDTTestDTO kdtTestDTO){
        // 시험 제목 변경
        if(kdtTestDTO.getKdtTestTitle() != null){
            this.kdtTestTitle = kdtTestDTO.getKdtTestTitle();
        }
        // 시험 시작 시간 변경
        if(kdtTestDTO.getKdtTestStartDate() !=null){
            this.kdtTestStartDate = kdtTestDTO.getKdtTestStartDate();
        }
        // 시험 종료 시간 변경
        if(kdtTestDTO.getKdtTestStartDate() !=null){
            this.kdtTestEndDate = kdtTestDTO.getKdtTestEndDate();
        }

        // 업데이트 시간 변경
        if(kdtTestDTO.getKdtTestUpdatedAt() != null){
            this.kdtTestUpdatedAt = kdtTestDTO.getKdtTestUpdatedAt();
        }
    }
}
