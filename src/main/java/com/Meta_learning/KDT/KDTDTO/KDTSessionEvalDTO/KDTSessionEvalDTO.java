package com.Meta_learning.KDT.KDTDTO.KDTSessionEvalDTO;

import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEvalEntity.KDTSessionEvalEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTSessionEvalDTO {

    private Long kdtSessionEvalId;  // 설문 평가 ID
    private Long kdtPartId;  // 참가자 ID (KDTPartEntity 참조)

    private int kdtSessionEvalRating1;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating2;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating3;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating4;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating5;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating6;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating7;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating8;  // 기본값 설정 (1~5)
    private int kdtSessionEvalRating9;  // 기본값 설정 (1~5)

    private String kdtSessionEvalReview;  // 총평

    private LocalDateTime kdtSessionEvalCreatedAt;  // 설문 생성일자
    private LocalDateTime kdtSessionEvalUpdatedAt;  // 설문 수정일자

    // 설문을 Entity로 변환하는 메소드
    public static KDTSessionEvalDTO fromEntity(KDTSessionEvalEntity entity) {
        return KDTSessionEvalDTO.builder()
                .kdtSessionEvalId(entity.getKdtSessionEvalId())
                .kdtPartId(entity.getKdtPartEntity() != null ? entity.getKdtPartEntity().getKdtPartId() : null)
                .kdtSessionEvalRating1(entity.getKdtSessionEvalRating1())
                .kdtSessionEvalRating2(entity.getKdtSessionEvalRating2())
                .kdtSessionEvalRating3(entity.getKdtSessionEvalRating3())
                .kdtSessionEvalRating4(entity.getKdtSessionEvalRating4())
                .kdtSessionEvalRating5(entity.getKdtSessionEvalRating5())
                .kdtSessionEvalRating6(entity.getKdtSessionEvalRating6())
                .kdtSessionEvalRating7(entity.getKdtSessionEvalRating7())
                .kdtSessionEvalRating8(entity.getKdtSessionEvalRating8())
                .kdtSessionEvalRating9(entity.getKdtSessionEvalRating9())
                .kdtSessionEvalReview(entity.getKdtSessionEvalReview())
                .kdtSessionEvalCreatedAt(entity.getKdtSessionEvalCreatedAt())
                .kdtSessionEvalUpdatedAt(entity.getKdtSessionEvalUpdatedAt())
                .build();
    }

    // DTO를 Entity로 변환하는 메소드
    public KDTSessionEvalEntity toEntity(KDTPartEntity kdtPartEntity) {
        return KDTSessionEvalEntity.builder()
                .kdtSessionEvalId(this.kdtSessionEvalId)
                .kdtPartEntity(kdtPartEntity)
                .kdtSessionEvalRating1(this.kdtSessionEvalRating1)
                .kdtSessionEvalRating2(this.kdtSessionEvalRating2)
                .kdtSessionEvalRating3(this.kdtSessionEvalRating3)
                .kdtSessionEvalRating4(this.kdtSessionEvalRating4)
                .kdtSessionEvalRating5(this.kdtSessionEvalRating5)
                .kdtSessionEvalRating6(this.kdtSessionEvalRating6)
                .kdtSessionEvalRating7(this.kdtSessionEvalRating7)
                .kdtSessionEvalRating8(this.kdtSessionEvalRating8)
                .kdtSessionEvalRating9(this.kdtSessionEvalRating9)
                .kdtSessionEvalReview(this.kdtSessionEvalReview)
                .kdtSessionEvalCreatedAt(this.kdtSessionEvalCreatedAt)
                .kdtSessionEvalUpdatedAt(this.kdtSessionEvalUpdatedAt)
                .build();
    }
}

