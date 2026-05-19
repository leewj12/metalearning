package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.course.courseentity.CourseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class CourseDescriptCreateServiceRequest {
    private CourseEntity courseEntity;
    private String courseDescriptContent;
    private List<MultipartFile> courseDescriptFiles;

    @Builder
    public CourseDescriptCreateServiceRequest(CourseEntity courseEntity, String courseDescriptContent, List<MultipartFile> courseDescriptFiles) {
        this.courseEntity = courseEntity;
        this.courseDescriptContent = courseDescriptContent;
        this.courseDescriptFiles = courseDescriptFiles;
    }
}
