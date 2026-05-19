package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.course.courseentity.CourseEntity;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class CourseDetailCreateServiceRequest {

    private CourseEntity courseEntity;
    private String courseDetailOutline;
    private String courseDetailTitle;
    private String courseDetailContent;
    private MultipartFile courseDetailFile;
    private String videoUrl;

    @Builder
    public CourseDetailCreateServiceRequest(CourseEntity courseEntity, String courseDetailOutline, String courseDetailTitle, String courseDetailContent, MultipartFile courseDetailFile, String videoUrl) {
        this.courseEntity = courseEntity;
        this.courseDetailOutline = courseDetailOutline;
        this.courseDetailTitle = courseDetailTitle;
        this.courseDetailContent = courseDetailContent;
        this.courseDetailFile = courseDetailFile;
        this.videoUrl = videoUrl;
    }
}
