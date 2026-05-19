package com.Meta_learning.KDT.KDTentity.KDTTestEntity;

import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestGradingDTO;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_test_grading")
@Getter
public class KDTTestGradingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kdt_test_grading_id")
    private Long kdtTestGradingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_test_submit_id", referencedColumnName = "KDT_test_submit_id", nullable = false)
    private KDTTestSubmitEntity kdtTestSubmitEntity;  // 시험 답안 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity;  // 채점자

    @Column(name = "kdt_test_grading_score", nullable = false)
    private int kdtTestGradingScore;

    @Column(name = "kdt_test_grading_created_at", nullable = false, updatable = false)
    private LocalDateTime kdtTestGradingCreatedAt;

    @Column(name = "kdt_test_grading_updated_at")
    private LocalDateTime kdtTestGradingUpdatedAt;

    @Column(name = "kdt_test_grading_comment")
    private String kdtTestGradingComment;

    public void update(KDTTestGradingDTO kdtTestGradingDTO){
        // 점수 변경
        this.kdtTestGradingScore = kdtTestGradingDTO.getKdtTestGradingScore();

        // 업데이트 시간 변경
        if(kdtTestGradingDTO.getKdtTestGradingUpdatedAt() != null){
            this.kdtTestGradingUpdatedAt = kdtTestGradingDTO.getKdtTestGradingUpdatedAt();
        }

        // 코멘트 변경
        if(kdtTestGradingDTO.getKdtTestGradingComment() != null) {
            this.kdtTestGradingComment = kdtTestGradingDTO.getKdtTestGradingComment();
        }
    }
}
