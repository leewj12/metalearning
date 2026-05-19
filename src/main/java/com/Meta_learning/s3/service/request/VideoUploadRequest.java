package com.Meta_learning.s3.service.request;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class VideoUploadRequest {
    private KDTCourseOutlineEntity kdtCourseOutlineEntity; // KDT_course_outline 테이블과 연관
    private UserEntity userEntity;  // 관리자, 매니저
    private Long courseOutlineId;
    private String category;
    private String title;
    private MultipartFile file;
    private String videoUrl;

    @Builder
    public VideoUploadRequest(KDTCourseOutlineEntity kdtCourseOutlineEntity, UserEntity userEntity, Long courseOutlineId, String category, String title, MultipartFile file, String videoUrl) {
        this.kdtCourseOutlineEntity = kdtCourseOutlineEntity;
        this.userEntity = userEntity;
        this.courseOutlineId = courseOutlineId;
        this.category = category;
        this.title = title;
        this.file = file;
        this.videoUrl = videoUrl;

        validate();
    }

    /**
     * 파일 업로드 또는 URL 중 하나는 필수
     */
    private void validate() {
        if ((file == null || file.isEmpty()) && (videoUrl == null || videoUrl.trim().isEmpty())) {
            throw new IllegalArgumentException("파일 업로드 또는 유효한 동영상 URL이 필요합니다.");
        }
    }

    /**
     * 파일 업로드 여부 확인
     */
    public boolean isFileUpload() {
        return file != null && !file.isEmpty();
    }

    /**
     * URL 업로드 여부 확인
     */
    public boolean isUrlUpload() {
        return videoUrl != null && !videoUrl.trim().isEmpty();
    }
}