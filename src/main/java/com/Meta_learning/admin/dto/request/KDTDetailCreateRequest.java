package com.Meta_learning.admin.dto.request;


import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class KDTDetailCreateRequest {

    @NotNull(message = "국비 회차 ID는 필수입니다.")
    private Long kdtSessionId;  // 국비회차id

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;           // 국비 담당자 user_id()

    @NotBlank(message = "상세 내용은 필수입니다.")
    private String kdtDetailContent;            // 상세 내용

    // 파일 엔티티 리스트
    private List<KDTDetailFileEntity> fileEntities;

    @Builder
    public KDTDetailCreateRequest(Long kdtSessionId, Long userId, String kdtDetailContent, List<KDTDetailFileEntity> fileEntities) {
        this.kdtSessionId = kdtSessionId;
        this.userId = userId;
        this.kdtDetailContent = kdtDetailContent;
        this.fileEntities = fileEntities;
    }

    /**
     * Service 요청 객체로 변환
     *
     * @param userEntity     사용자 엔티티
     * @param sessionEntity  국비 세션 엔티티
     * @param fileEntities   업로드된 파일 엔티티 리스트
     * @return KDTDetailCreateServiceRequest
     */
    public KDTDetailCreateServiceRequest toServiceRequest(UserEntity userEntity, KDTSessionEntity sessionEntity, List<KDTDetailFileEntity> fileEntities) {
        return KDTDetailCreateServiceRequest.builder()
                .kdtSessionEntity(sessionEntity)
                .userEntity(userEntity)
                .kdtDetailContent(kdtDetailContent)
                .fileEntities(fileEntities)
                .build();
    }
}
