package com.Meta_learning.KDT.KDTDTO.KDTPartDTO;


import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter

public class KDTPartDTO {

    private Long kdtPartId;             // KDTPart ID
    private Long kdtSessionId;          // 국비회차 ID
    private Long userId;                // 담당자 user_id
    private String kdtPartStatus;       // 수업 상태 (대기, 수료중 등) -> String
    private Boolean kdtPartEmp;         // 취업 상태 (True/False)

    private List<Long> userIds;         // 여러 강사 ID를 받을 수 있도록 List 사용

    // Entity -> DTO 변환
    public static KDTPartDTO fromEntity(KDTPartEntity entity) {
        return KDTPartDTO.builder()
                .kdtPartId(entity.getKdtPartId())
                .kdtSessionId(entity.getKdtSessionEntity().getKdtSessionId())  // 연관된 KDTSession ID
                .userId(entity.getUserEntity().getUserId())  // 연관된 User ID
                .kdtPartStatus(entity.getKdtPartStatus() != null ? entity.getKdtPartStatus().getText() : null) // Enum -> String 변환
                .kdtPartEmp(entity.getKdtPartEmp())
                .build();
    }
}
