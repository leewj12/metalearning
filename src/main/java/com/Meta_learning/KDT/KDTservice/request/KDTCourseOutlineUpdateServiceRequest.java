package com.Meta_learning.KDT.KDTservice.request;


import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KDTCourseOutlineUpdateServiceRequest {
    private Long kdtCourseOutlineId;
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id
    private String kdtCourseOutline;        // 국비강의 목차 String

    @Builder
    public KDTCourseOutlineUpdateServiceRequest(Long kdtCourseOutlineId, KDTSessionEntity kdtSessionEntity, String kdtCourseOutline) {
        this.kdtCourseOutlineId = kdtCourseOutlineId;
        this.kdtSessionEntity = kdtSessionEntity;
        this.kdtCourseOutline = kdtCourseOutline;
    }
}
