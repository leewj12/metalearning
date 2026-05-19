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
public class VideoUpdateRequest {
    private String uploadType;
    private Long kdtCourseVideoId;
    private KDTCourseOutlineEntity kdtCourseOutlineEntity; // KDT_course_outline 테이블과 연관
    private UserEntity userEntity;  // 관리자, 매니저
    private Long courseOutlineId;
    private String category;
    private String title;
    private MultipartFile file;
    private String videoUrl;

    @Builder
    public VideoUpdateRequest(String uploadType, Long kdtCourseVideoId, KDTCourseOutlineEntity kdtCourseOutlineEntity, UserEntity userEntity, Long courseOutlineId, String category, String title, MultipartFile file, String videoUrl) {
        this.uploadType = uploadType;
        this.kdtCourseVideoId = kdtCourseVideoId;
        this.kdtCourseOutlineEntity = kdtCourseOutlineEntity;
        this.userEntity = userEntity;
        this.courseOutlineId = courseOutlineId;
        this.category = category;
        this.title = title;
        this.file = file;
        this.videoUrl = videoUrl;
    }
}