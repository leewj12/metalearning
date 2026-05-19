package com.Meta_learning.course.coursecontroller.dto.response;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import com.Meta_learning.course.courseentity.CourseStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class CourseUpdateResponse {

    @NotNull(message = "강의 ID는 필수 항목입니다.")
    private Long courseId;

    @NotNull(message = "강의 설명 ID는 필수 항목입니다.")
    private Long courseDescriptId;

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

    @NotBlank(message = "강의 설명 내용은 필수 항목입니다.")
    private String courseDescriptContent;

    @NotNull(message = "강의 설명 파일은 필수 항목입니다.")
    private List<CourseDescriptFileUpdateResponse> courseDescriptFiles;

    @NotNull(message = "강의 상태는 필수 항목입니다.")
    private CourseStatus courseStatus; // 기본값: 대기

    @NotNull(message = "강의 세부 정보는 필수 항목입니다.")
    @Valid
    private List<CourseDetailUpdateResponse> courseDetails = new ArrayList<>();

    @Builder
    public CourseUpdateResponse(Long courseId, Long courseDescriptId, String courseTitle, String courseDescript, Long coursePrice, String courseCategory, CourseDifficulty courseDifficulty, String courseThumbnail, String courseDescriptContent, List<CourseDescriptFileUpdateResponse> courseDescriptFiles, CourseStatus courseStatus, List<CourseDetailUpdateResponse> courseDetails) {
        this.courseId = courseId;
        this.courseDescriptId = courseDescriptId;
        this.courseTitle = courseTitle;
        this.courseDescript = courseDescript;
        this.coursePrice = coursePrice;
        this.courseCategory = courseCategory;
        this.courseDifficulty = courseDifficulty;
        this.courseThumbnail = courseThumbnail;
        this.courseDescriptContent = courseDescriptContent;
        this.courseDescriptFiles = courseDescriptFiles;
        this.courseStatus = courseStatus;
        this.courseDetails = courseDetails;
    }

//    public CourseUpdateServiceRequest toCourseServiceRequest(InstrEntity instrEntity) {
//        return CourseUpdateServiceRequest.builder()
//                .instr(instrEntity)
//                .courseThumbnail(courseThumbnail)
//                .courseTitle(courseTitle)
//                .courseDescript(courseDescript)
//                .coursePrice(coursePrice)
//                .courseCategory(courseCategory)
//                .courseDifficulty(courseDifficulty)
//                .courseStatus(courseStatus)
//                .build();
//    }

//    public CourseDescriptUpdateServiceRequest toCourseDescriptServiceRequest(CourseEntity courseEntity) {
//        return CourseDescriptUpdateServiceRequest.builder()
//                .courseEntity(courseEntity)
//                .courseDescriptContent(courseDescriptContent)
//                .courseDescriptFiles(courseDescriptFiles)
//                .build();
//    }

//    // 변환 메서드: CourseDetailRequest -> CourseDetailCreateServiceRequest
//    public List<CourseDetailUpdateServiceRequest> toCourseDetailServiceRequests(CourseEntity courseEntity) {
//        return courseDetails.stream()
//                .map(detail -> CourseDetailUpdateServiceRequest.builder()
//                        .courseEntity(courseEntity)
//                        .courseDetailOutline(detail.getCourseDetailOutline())
//                        .courseDetailTitle(detail.getCourseDetailTitle())
//                        .courseDetailContent(detail.getCourseDetailContent())
//                        .courseDetailFile(detail.getCourseDetailFileUUID())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
