package com.Meta_learning.course.coursecontroller.dto.response;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import com.Meta_learning.course.courseentity.CourseStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseListResponse {

        @NotNull(message = "강의 ID는 필수 항목입니다.")
        private Long courseId;

        @NotBlank(message = "강의 제목은 필수 항목입니다.")
        private String courseTitle;

        @NotBlank(message = "강의 설명은 필수 항목입니다.")
        private String courseDescript;

        @NotNull(message = "강의 가격은 필수 항목입니다.")
        @Min(value = 0, message = "가격은 0 이상의 값을 입력해야 합니다.")
        private Long coursePrice;

        @NotBlank(message = "강의 카테고리는 필수 항목입니다.")
        private String courseCategory;

        @NotNull(message = "강의 난이도를 선택해야 합니다.")
        private CourseDifficulty courseDifficulty;

        @NotNull(message = "강의 썸네일은 필수 항목입니다.")
        private String courseThumbnail;

        @NotNull(message = "강의 상태는 필수 항목입니다.")
        private CourseStatus courseStatus; // 기본값: 대기

    @Builder
    public CourseListResponse(Long courseId, String courseTitle, String courseDescript, Long coursePrice, String courseCategory, CourseDifficulty courseDifficulty, String courseThumbnail, CourseStatus courseStatus) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescript = courseDescript;
        this.coursePrice = coursePrice;
        this.courseCategory = courseCategory;
        this.courseDifficulty = courseDifficulty;
        this.courseThumbnail = courseThumbnail;
        this.courseStatus = courseStatus;
    }
}
