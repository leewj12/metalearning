package com.Meta_learning.KDT.KDTservice.response;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KDTCourseOutlineResponse {

    private Long kdtCourseOutlineId;
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id
    private String kdtCourseOutline;        // 국비강의 목차 String

    @Builder
    private KDTCourseOutlineResponse(Long kdtCourseOutlineId, KDTSessionEntity kdtSessionEntity, String kdtCourseOutline) {
        this.kdtCourseOutlineId = kdtCourseOutlineId;
        this.kdtSessionEntity = kdtSessionEntity;
        this.kdtCourseOutline = kdtCourseOutline;
    }

    public static KDTCourseOutlineResponse of(KDTCourseOutlineEntity kdtCourseOutlineEntity) {
        return KDTCourseOutlineResponse.builder()
                .kdtCourseOutlineId(kdtCourseOutlineEntity.getKdtCourseOutlineId())
                .kdtSessionEntity(kdtCourseOutlineEntity.getKdtSessionEntity())
                .kdtCourseOutline(kdtCourseOutlineEntity.getKdtCourseOutline())
                .build();
    }
}
