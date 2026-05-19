package com.Meta_learning.course.coursecontroller.dto.update;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdateResponseDTO {
    private Long courseId;
    private String courseThumbnail;
    private String courseTitle;
    private String courseDescript;
    private CourseDifficulty courseDifficulty;
    private String courseCategory;
    private Long coursePrice;       // 0이상 validation

    @Builder
    public CourseUpdateResponseDTO(Long courseId, String courseThumbnail, String courseTitle, String courseDescript, CourseDifficulty courseDifficulty, String courseCategory, Long coursePrice) {
        this.courseId = courseId;
        this.courseThumbnail = courseThumbnail;
        this.courseTitle = courseTitle;
        this.courseDescript = courseDescript;
        this.courseDifficulty = courseDifficulty;
        this.courseCategory = courseCategory;
        this.coursePrice = coursePrice;
    }
}
