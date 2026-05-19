package com.Meta_learning.KDT.KDTservice.request;


import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KDTCourseOutlineCreateServiceRequest {
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id
    private String kdtCourseOutline;        // 국비강의 목차 String

    @Builder
    public KDTCourseOutlineCreateServiceRequest(KDTSessionEntity kdtSessionEntity, String kdtCourseOutline) {
        this.kdtSessionEntity = kdtSessionEntity;
        this.kdtCourseOutline = kdtCourseOutline;
    }
}
