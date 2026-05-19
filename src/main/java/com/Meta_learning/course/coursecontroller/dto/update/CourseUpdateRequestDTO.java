package com.Meta_learning.course.coursecontroller.dto.update;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CourseUpdateRequestDTO {
    private Long courseId;
    private MultipartFile courseThumbnail;
    private String courseTitle;
    private String courseDescript;
    private CourseDifficulty courseDifficulty;
    private String courseCategory;
    private Long coursePrice;       // 0이상 validation

    @Builder
    public CourseUpdateRequestDTO(Long courseId, MultipartFile courseThumbnail, String courseTitle, String courseDescript, CourseDifficulty courseDifficulty, String courseCategory, Long coursePrice) {
        this.courseId = courseId;
        this.courseThumbnail = courseThumbnail;
        this.courseTitle = courseTitle;
        this.courseDescript = courseDescript;
        this.courseDifficulty = courseDifficulty;
        this.courseCategory = courseCategory;
        this.coursePrice = coursePrice;
    }
}
