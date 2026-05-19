package com.Meta_learning.admin.dto.request;


import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class KDTCourseOutlineCreateRequest {

    @NotNull(message = "국비 회차 ID는 필수입니다.")
    private Long kdtSessionId;  // 국비회차id

    @NotBlank(message = "국비 강의 목차 제목은 필수입니다.")
    private String kdtCourseOutline;            // 상세 내용


    @Builder
    public KDTCourseOutlineCreateRequest(Long kdtSessionId, String kdtCourseOutline) {
        this.kdtSessionId = kdtSessionId;
        this.kdtCourseOutline = kdtCourseOutline;
    }

    public KDTCourseOutlineCreateServiceRequest toServiceRequest(KDTSessionEntity sessionEntity) {
        return KDTCourseOutlineCreateServiceRequest.builder()
                .kdtSessionEntity(sessionEntity)
                .kdtCourseOutline(kdtCourseOutline)
                .build();
    }
}
