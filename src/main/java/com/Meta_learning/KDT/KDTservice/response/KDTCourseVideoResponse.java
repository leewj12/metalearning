package com.Meta_learning.KDT.KDTservice.response;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class KDTCourseVideoResponse {

    private Long kdtCourseVideoId;
    private KDTCourseOutlineEntity kdtCourseOutlineEntity; // KDT_course_outline 테이블과 연관
    private UserEntity userEntity;  // 관리자, 매니저
    private String kdtCourseVideoCategory;
    private String kdtCourseVideoTitle;
    private String kdtCourseVideoFile;
    private String kdtCourseVideoUUID; // URL 링크도 여기에 포함됨
    private LocalDateTime kdtCourseVideoCreatedAt; // 동영상 업로드 시간

    @Builder
    public KDTCourseVideoResponse(Long kdtCourseVideoId, KDTCourseOutlineEntity kdtCourseOutlineEntity, UserEntity userEntity, String kdtCourseVideoCategory, String kdtCourseVideoTitle, String kdtCourseVideoFile, String kdtCourseVideoUUID, LocalDateTime kdtCourseVideoCreatedAt) {
        this.kdtCourseVideoId = kdtCourseVideoId;
        this.kdtCourseOutlineEntity = kdtCourseOutlineEntity;
        this.userEntity = userEntity;
        this.kdtCourseVideoCategory = kdtCourseVideoCategory;
        this.kdtCourseVideoTitle = kdtCourseVideoTitle;
        this.kdtCourseVideoFile = kdtCourseVideoFile;
        this.kdtCourseVideoUUID = kdtCourseVideoUUID;
        this.kdtCourseVideoCreatedAt = kdtCourseVideoCreatedAt;
    }

    public static KDTCourseVideoResponse of(KDTCourseVideoEntity kdtCourseVideoEntity) {
        return KDTCourseVideoResponse.builder()
                .kdtCourseVideoId(kdtCourseVideoEntity.getKdtCourseVideoId())
                .kdtCourseOutlineEntity(kdtCourseVideoEntity.getKdtCourseOutlineEntity())
                .userEntity(kdtCourseVideoEntity.getUserEntity())
                .kdtCourseVideoCategory(kdtCourseVideoEntity.getKdtCourseVideoCategory())
                .kdtCourseVideoTitle(kdtCourseVideoEntity.getKdtCourseVideoTitle())
                .kdtCourseVideoFile(kdtCourseVideoEntity.getKdtCourseVideoFile())
                .kdtCourseVideoUUID(kdtCourseVideoEntity.getKdtCourseVideoUUID())
                .kdtCourseVideoCreatedAt(kdtCourseVideoEntity.getKdtCourseVideoCreatedAt())
                .build();
    }
}
