package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.course.courseentity.CourseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
public class CourseDetailUpdateServiceRequest {

    private Long courseDetailId;
    private CourseEntity courseEntity;
    private String courseDetailOutline;
    private String courseDetailTitle;
    private String courseDetailContent;
    private MultipartFile courseDetailFile;
    private String videoUrl;

    @Builder
    public CourseDetailUpdateServiceRequest(Long courseDetailId, CourseEntity courseEntity, String courseDetailOutline, String courseDetailTitle, String courseDetailContent, MultipartFile courseDetailFile, String videoUrl) {
        this.courseDetailId = courseDetailId;
        this.courseEntity = courseEntity;
        this.courseDetailOutline = courseDetailOutline;
        this.courseDetailTitle = courseDetailTitle;
        this.courseDetailContent = courseDetailContent;
        this.courseDetailFile = courseDetailFile;
        this.videoUrl = videoUrl;
    }
}
