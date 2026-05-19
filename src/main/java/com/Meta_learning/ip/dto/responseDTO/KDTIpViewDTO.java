package com.Meta_learning.ip.dto.responseDTO;

import com.Meta_learning.ip.entity.KDTIpEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KDTIpViewDTO {

    private Long kdtIpId;
    private String kdtipAddress;
    private LocalDateTime kdtIpCreatedAt;
    private LocalDateTime kdtIpUpdatedAt;

    // Entity -> DTO 변환 메서드
    public static KDTIpViewDTO toDTO(KDTIpEntity kdtIpEntity) {
        // 변수를 생성하여 엔티티의 값을 DTO로 설정
        KDTIpViewDTO kdtIpViewDTO = new KDTIpViewDTO(
                kdtIpEntity.getKdtIpId(),           // 엔티티의 ID를 DTO에 설정
                kdtIpEntity.getKdtIpAddress(),      // 엔티티의 IP 주소를 DTO에 설정
                kdtIpEntity.getKdtIpCreatedAt(),    // 엔티티의 생성 날짜를 DTO에 설정
                kdtIpEntity.getKdtIpUpdatedAt()     // 엔티티의 수정 날짜를 DTO에 설정
        );

        // 생성된 DTO 반환
        return kdtIpViewDTO;
    }
}
