package com.Meta_learning.KDT.KDTentity.KDTSessionEvalEntity;

import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "KDT_session_eval")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class KDTSessionEvalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_session_eval_id")
    private Long kdtSessionEvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_part_id", referencedColumnName = "KDT_part_id", nullable = false)
    private KDTPartEntity kdtPartEntity;  // 국비 참가자 정보 (참여자)

    @Column(name = "KDT_session_eval_rating1", nullable = false)
    private int kdtSessionEvalRating1; // 강사 만족도 (1~5)

    @Column(name = "KDT_session_eval_rating2", nullable = false)
    private int kdtSessionEvalRating2; // 교육 콘텐츠 품질 (1~5)

    @Column(name = "KDT_session_eval_rating3", nullable = false)
    private int kdtSessionEvalRating3; // 교육 효과 (1~5)

    @Column(name = "KDT_session_eval_rating4", nullable = false)
    private int kdtSessionEvalRating4; // 교육 환경 (1~5)

    @Column(name = "KDT_session_eval_rating5", nullable = false)
    private int kdtSessionEvalRating5; // 학습 지원 (1~5)

    @Column(name = "KDT_session_eval_rating6", nullable = false)
    private int kdtSessionEvalRating6; // 교육 참여도 (1~5)

    @Column(name = "KDT_session_eval_rating7", nullable = false)
    private int kdtSessionEvalRating7; // 교육 후 피드백 (1~5)

    @Column(name = "KDT_session_eval_rating8", nullable = false)
    private int kdtSessionEvalRating8; // 교육 후 취업/진로 연계 (1~5)

    @Column(name = "KDT_session_eval_rating9", nullable = false)
    private int kdtSessionEvalRating9; // 전반적인 만족도 (1~5)

    @Column(name = "KDT_session_eval_review", nullable = true)
    private String kdtSessionEvalReview; // 총평 (선택사항)

    @CreatedDate
    @Column(name = "KDT_session_eval_created_at", updatable = false, nullable = false)
    private LocalDateTime kdtSessionEvalCreatedAt;  // 평가 생성일자

    @LastModifiedDate
    @Column(name = "KDT_session_eval_updated_at")
    private LocalDateTime kdtSessionEvalUpdatedAt;  // 평가 수정일자
}
