package com.Meta_learning.KDT.KDTentity.KDTTestEntity;

import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_test_submit")
@Getter
public class KDTTestSubmitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // test_submit_id가 기본 키
    @Column(name = "KDT_test_submit_id")
    private Long kdtTestSubmitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_test_item_id", referencedColumnName = "KDT_test_item_id", nullable = false)
    private KDTTestItemEntity kdtTestItemEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_part_id", referencedColumnName = "KDT_part_id", nullable = false)
    private KDTPartEntity kdtPartEntity;  // 국비 참가자 id

    @Column(name = "KDT_test_submit_answer", nullable = false)
    private int kdtTestSubmitAnswer;

    @Column(name = "KDT_test_submit_created_at", nullable = false, updatable = false)
    private LocalDateTime kdtTestSubmitCreatedAt;

    @Column(name = "KDT_test_submit_updated_at")
    private LocalDateTime kdtTestSubmitUpdatedAt;
}
