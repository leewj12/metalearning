package com.Meta_learning.KDT.KDTentity.KDTTestEntity;

import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestItemDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_test_item")
@Getter
public class KDTTestItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kdt_test_item_id")
    private Long kdtTestItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_test_id", referencedColumnName = "KDT_test_id", nullable = false)
    private KDTTestEntity kdtTestEntity;  // 시험id

    @Column(name="KDT_test_item_quest", nullable=false)
    private String kdtTestItemQuest;

    @Column(name="KDT_test_item_answer", nullable=false)
    private int kdtTestItemAnswer;

    @Column(name="KDT_test_item_answ1", nullable=false)
    private String kdtTestItemAnsw1;

    @Column(name="KDT_test_item_answ2", nullable=false)
    private String kdtTestItemAnsw2;

    @Column(name="KDT_test_item_answ3", nullable=false)
    private String kdtTestItemAnsw3;

    @Column(name="KDT_test_item_answ4", nullable=false)
    private String kdtTestItemAnsw4;

    @Column(name="KDT_test_item_score", nullable=false)
    private int kdtTestItemScore;

    @Column(name="KDT_test_item_category", nullable=false)
    private String kdtTestItemCategory;

    public void update(KDTTestItemDTO kdtTestItemDTO){
        // 시험 문제 변경
        if(kdtTestItemDTO.getKdtTestItemQuest() != null){
            this.kdtTestItemQuest = kdtTestItemDTO.getKdtTestItemQuest();
        }

        // 시험 정답 변경
        this.kdtTestItemAnswer = kdtTestItemDTO.getKdtTestItemAnswer();

        // 보기 1, 2, 3, 4
        if(kdtTestItemDTO.getKdtTestItemAnsw1() != null){
            this.kdtTestItemAnsw1 = kdtTestItemDTO.getKdtTestItemAnsw1();
        }

        if(kdtTestItemDTO.getKdtTestItemAnsw2() != null){
            this.kdtTestItemAnsw2 = kdtTestItemDTO.getKdtTestItemAnsw2();
        }

        if(kdtTestItemDTO.getKdtTestItemAnsw3() != null){
            this.kdtTestItemAnsw3 = kdtTestItemDTO.getKdtTestItemAnsw3();
        }

        if(kdtTestItemDTO.getKdtTestItemAnsw4() != null){
            this.kdtTestItemAnsw4 = kdtTestItemDTO.getKdtTestItemAnsw4();
        }

        // 점수
        this.kdtTestItemScore = kdtTestItemDTO.getKdtTestItemScore();

        // 카테고리
        if(kdtTestItemDTO.getKdtTestItemCategory() != null){
            this.kdtTestItemCategory = kdtTestItemDTO.getKdtTestItemCategory();
        }
    }
}
