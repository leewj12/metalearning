package com.Meta_learning.course.coursecontroller.dto.update;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CourseDetailUpdateRequestDTO {
    private String uploadType; // "file" 또는 "embed"
    private Long courseDetailId;
    private Long courseFileId;
    private Long courseVideoId;
    private String courseDetailTitle;
    private String courseVideoUrl; // S3 URL 추가
    private MultipartFile courseDetailFile;

    @Builder
    public CourseDetailUpdateRequestDTO(String uploadType, Long courseDetailId, Long courseFileId, Long courseVideoId, String courseDetailTitle, String courseVideoUrl, MultipartFile courseDetailFile) {
        this.uploadType = uploadType;
        this.courseDetailId = courseDetailId;
        this.courseFileId = courseFileId;
        this.courseVideoId = courseVideoId;
        this.courseDetailTitle = courseDetailTitle;
        this.courseVideoUrl = courseVideoUrl;
        this.courseDetailFile = courseDetailFile;
    }
}


