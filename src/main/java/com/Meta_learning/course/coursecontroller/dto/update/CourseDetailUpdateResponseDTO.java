package com.Meta_learning.course.coursecontroller.dto.update;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CourseDetailUpdateResponseDTO {
    private Long courseDetailId;
    private Long courseFileId;
    private Long courseVideoId;
    private String courseDetailTitle;
    private String courseDetailFileUUID;        // NULL이면 FILE임
    private String courseVideoUrl; // S3 URL 추가

    @Builder
    public CourseDetailUpdateResponseDTO(Long courseDetailId, Long courseFileId, Long courseVideoId, String courseDetailTitle, String courseDetailFileUUID, String courseVideoUrl) {
        this.courseDetailId = courseDetailId;
        this.courseFileId = courseFileId;
        this.courseVideoId = courseVideoId;
        this.courseDetailTitle = courseDetailTitle;
        this.courseDetailFileUUID = courseDetailFileUUID;
        this.courseVideoUrl = courseVideoUrl;
    }
}


