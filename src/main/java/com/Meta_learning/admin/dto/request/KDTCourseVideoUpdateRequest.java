package com.Meta_learning.admin.dto.request;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class KDTCourseVideoUpdateRequest {

    private String uploadType; // "file" 또는 "embed"

    @NotNull(message = "국비 강의 ID는 필수입니다.")
    private Long kdtCourseVideoId;

    @NotNull(message = "국비 강의 목록 ID는 필수입니다.")
    private Long kdtCourseOutlineId;

//    @NotBlank(message = "강의 카테고리는 필수입니다.")
    private String category;

    @NotBlank(message = "강의 제목은 필수입니다.")
    private String title;

//    @NotNull(message = "강의 동영상은 필수입니다.")
    private MultipartFile file;

    private String videoUrl;

    @Builder
    public KDTCourseVideoUpdateRequest(String uploadType, Long kdtCourseVideoId, Long kdtCourseOutlineId, String category, String title, MultipartFile file, String videoUrl) {
        this.uploadType = uploadType;
        this.kdtCourseVideoId = kdtCourseVideoId;
        this.kdtCourseOutlineId = kdtCourseOutlineId;
        this.category = category;
        this.title = title;
        this.file = file;
        this.videoUrl = videoUrl;
    }

    public VideoUpdateRequest toServiceRequest(KDTCourseOutlineEntity kdtCourseOutlineEntity, UserEntity userEntity) {
        return VideoUpdateRequest.builder()
                .uploadType(uploadType)
                .kdtCourseVideoId(kdtCourseVideoId)
                .kdtCourseOutlineEntity(kdtCourseOutlineEntity)
                .userEntity(userEntity)
                .courseOutlineId(kdtCourseOutlineId)
                .category(category)
                .title(title)
                .file(file)
                .videoUrl(videoUrl)
                .build();
    }

    public VideoUploadRequest toServiceUploadRequest(KDTCourseOutlineEntity kdtCourseOutlineEntity, UserEntity userEntity) {
        return VideoUploadRequest.builder()
                .kdtCourseOutlineEntity(kdtCourseOutlineEntity)
                .userEntity(userEntity)
                .courseOutlineId(kdtCourseOutlineId)
                .category(category)
                .title(title)
                .file(file)
                .videoUrl(videoUrl)
                .build();
    }
}
