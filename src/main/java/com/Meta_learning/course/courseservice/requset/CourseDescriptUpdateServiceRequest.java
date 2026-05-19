package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.course.courseentity.CourseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CourseDescriptUpdateServiceRequest {
    private CourseEntity courseEntity;
    private String courseDescriptContent;
    private List<Long> filesToDelete; // 삭제할 파일 ID 목록
    private List<MultipartFile> newFiles;

    @Builder
    public CourseDescriptUpdateServiceRequest(CourseEntity courseEntity, String courseDescriptContent, List<Long> filesToDelete, List<MultipartFile> newFiles) {
        this.courseEntity = courseEntity;
        this.courseDescriptContent = courseDescriptContent;
        this.filesToDelete = filesToDelete;
        this.newFiles = newFiles;
    }
}
