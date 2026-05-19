package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import com.Meta_learning.course.courseentity.CourseStatus;
import com.Meta_learning.course.courseentity.InstrEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class CourseUpdateServiceRequest {

    private Long courseId;
    private InstrEntity instr; // 강사 ID
    private MultipartFile courseThumbnail;
    private String courseTitle;
    private String courseDescript;
    private Long coursePrice;
    private String courseCategory;
    private CourseDifficulty courseDifficulty = CourseDifficulty.BEGINNER; // 기본값: 입문
    private CourseStatus courseStatus = CourseStatus.PENDING;

    @Builder
    public CourseUpdateServiceRequest(Long courseId, InstrEntity instr, MultipartFile courseThumbnail, String courseTitle, String courseDescript, Long coursePrice, String courseCategory, CourseDifficulty courseDifficulty, CourseStatus courseStatus) {
        this.courseId = courseId;
        this.instr = instr;
        this.courseThumbnail = courseThumbnail;
        this.courseTitle = courseTitle;
        this.courseDescript = courseDescript;
        this.coursePrice = coursePrice;
        this.courseCategory = courseCategory;
        this.courseDifficulty = courseDifficulty;
        this.courseStatus = courseStatus;
    }
}
