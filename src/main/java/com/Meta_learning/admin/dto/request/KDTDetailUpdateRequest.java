package com.Meta_learning.admin.dto.request;


import com.Meta_learning.KDT.KDTservice.request.KDTDetailUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class KDTDetailUpdateRequest {

//    @NotNull(message = "강의 ID는 필수입니다.")
//    private Long courseId;

    @NotNull(message = "상세 ID는 필수입니다.")
    private Long kdtDetailId;

    @NotBlank(message = "상세 내용은 필수입니다.")
    private String kdtDetailContent; // 수정 가능한 필드

    // 삭제할 파일 ID 리스트
    private List<Long> deleteFileIds;

    // 새로 추가할 파일 리스트
    private MultipartFile[] newFiles;

    @Builder
    private KDTDetailUpdateRequest(Long kdtDetailId, String kdtDetailContent, List<Long> deleteFileIds, MultipartFile[] newFiles) {
        this.kdtDetailId = kdtDetailId;
        this.kdtDetailContent = kdtDetailContent;
        this.deleteFileIds = deleteFileIds;
        this.newFiles = newFiles;
    }

    public KDTDetailUpdateServiceRequest toServiceRequest() {
        return KDTDetailUpdateServiceRequest.builder()
                .kdtDetailContent(kdtDetailContent)
                .build();
    }
}
