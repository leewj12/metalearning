package com.Meta_learning.KDT.KDTservice.response;


import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

//@Builder
@Getter
public class KDTDetailResponse {

    private Long kdtDetailId;
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id
    private UserEntity userEntity;              // 국비 담당자 user_id()
    private String kdtDetailContent;            // 상세 내용
    private LocalDateTime kdtDetailCreatedAt;   // 상세 작성일
    private LocalDateTime kdtDetailUpdatedAt;   // 상세 수정일
    private List<KDTDetailFileEntity> files; // 파일 리스트 추가

    // Getter and Setter for files
    public List<KDTDetailFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<KDTDetailFileEntity> files) {
        this.files = files;
    }

    @Builder
    private KDTDetailResponse(Long kdtDetailId, KDTSessionEntity kdtSessionEntity, UserEntity userEntity, String kdtDetailContent, LocalDateTime kdtDetailCreatedAt, LocalDateTime kdtDetailUpdatedAt, List<KDTDetailFileEntity> files) {
        this.kdtDetailId = kdtDetailId;
        this.kdtSessionEntity = kdtSessionEntity;
        this.userEntity = userEntity;
        this.kdtDetailContent = kdtDetailContent;
        this.kdtDetailCreatedAt = kdtDetailCreatedAt;
        this.kdtDetailUpdatedAt = kdtDetailUpdatedAt;
        this.files = files; // 파일 리스트 설정
    }

    public static KDTDetailResponse of(KDTDetailEntity kdtDetailEntity, List<KDTDetailFileEntity> files) {
        return KDTDetailResponse.builder()
                .kdtDetailId(kdtDetailEntity.getKdtDetailId())
                .kdtSessionEntity(kdtDetailEntity.getKdtSessionEntity())
                .userEntity(kdtDetailEntity.getUserEntity())
                .kdtDetailContent(kdtDetailEntity.getKdtDetailContent())
                .kdtDetailCreatedAt(kdtDetailEntity.getKdtDetailCreatedAt())
                .kdtDetailUpdatedAt(kdtDetailEntity.getKdtDetailUpdatedAt())
                .files(files)
                .build();
    }

    public static KDTDetailResponse of(KDTDetailEntity kdtDetailEntity) {
        return KDTDetailResponse.builder()
                .kdtDetailId(kdtDetailEntity.getKdtDetailId())
                .kdtSessionEntity(kdtDetailEntity.getKdtSessionEntity())
                .userEntity(kdtDetailEntity.getUserEntity())
                .kdtDetailContent(kdtDetailEntity.getKdtDetailContent())
                .kdtDetailCreatedAt(kdtDetailEntity.getKdtDetailCreatedAt())
                .kdtDetailUpdatedAt(kdtDetailEntity.getKdtDetailUpdatedAt())
                .files(kdtDetailEntity.getFiles())
                .build();
    }
}
