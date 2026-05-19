package com.Meta_learning.course.coursecontroller.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDetailUpdateResponse {
    private Long courseDetailId;
    private String courseDetailOutline;
    private String courseDetailTitle;
    private String courseDetailContent;
    private String courseDetailFileUUID;
    private Long courseFileId;
    private String courseVideoUrl; // S3 URL 추가
    private Long courseVideoId;
}
