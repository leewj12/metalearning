package com.Meta_learning.ip.dto.requestDTO;

import com.Meta_learning.ip.entity.KDTIpEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class KDTIpCreateDTO {

    private Long kdtSessionId;  // 회차 id
    private String kdtipAddress;

    // DTO -> Entity 변환 메서드
    public KDTIpEntity toEntity(KDTSessionEntity kdtSessionEntity) {
        return KDTIpEntity.builder()
                .kdtSessionEntity(kdtSessionEntity)  // KDTSessionEntity 설정
                .kdtIpAddress(kdtipAddress)          // IP 주소 설정
                .build();
    }
}
