package com.Meta_learning.KDT.KDTservice.request;


import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KDTDetailCreateServiceRequest {

    private KDTSessionEntity kdtSessionEntity;  // 국비회차id
    private UserEntity userEntity;              // 국비 담당자 user_id()
    private String kdtDetailContent;            // 상세 내용
    private List<KDTDetailFileEntity> fileEntities; // 파일 정보 추가

    @Builder
    public KDTDetailCreateServiceRequest(
            KDTSessionEntity kdtSessionEntity,
            UserEntity userEntity,
            String kdtDetailContent,
            List<KDTDetailFileEntity> fileEntities) {
        this.kdtSessionEntity = kdtSessionEntity;
        this.userEntity = userEntity;
        this.kdtDetailContent = kdtDetailContent;
        this.fileEntities = fileEntities;
    }

    /**
     * KDTDetailEntity 변환 메서드
     *
     * @return KDTDetailEntity
     */
    public KDTDetailEntity toEntity() {
        KDTDetailEntity kdtDetailEntity = KDTDetailEntity.builder()
                .kdtSessionEntity(kdtSessionEntity)
                .userEntity(userEntity)
                .kdtDetailContent(kdtDetailContent)
                .build();

        // 파일 리스트와 연관 관계 설정
        if (fileEntities != null && !fileEntities.isEmpty()) {
            for (KDTDetailFileEntity fileEntity : fileEntities) {
                fileEntity.setKdtDetailEntity(kdtDetailEntity); // 연관 관계 설정
            }
            kdtDetailEntity.getFiles().addAll(fileEntities); // 파일 리스트 추가
        }

        return kdtDetailEntity;
    }
}
