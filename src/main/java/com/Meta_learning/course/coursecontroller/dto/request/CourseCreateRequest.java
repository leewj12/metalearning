package com.Meta_learning.course.coursecontroller.dto.request;

import com.Meta_learning.course.courseentity.CourseDifficulty;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.CourseStatus;
import com.Meta_learning.course.courseentity.InstrEntity;
import com.Meta_learning.course.courseservice.requset.CourseCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDescriptCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDetailCreateServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CourseCreateRequest {
    @NotBlank(message = "강의 제목은 필수 항목입니다.")
    private String courseTitle;

    //@NotBlank(message = "강의 설명은 필수 항목입니다.")
    private String courseDescript;

    @NotNull(message = "강의 가격은 필수 항목입니다.")
    @Min(value = 0, message = "가격은 0 이상의 값을 입력해야 합니다.")
    private Long coursePrice;

    //@NotBlank(message = "강의 카테고리는 필수 항목입니다.")
    private String courseCategory;

    @NotNull(message = "강의 난이도를 선택해야 합니다.")
    private CourseDifficulty courseDifficulty;

//    @NotNull(message = "강의 썸네일은 필수 항목입니다.")
    private MultipartFile courseThumbnail;

    @NotBlank(message = "홍보글 상세 내용은 필수 항목입니다.")
    private String courseDescriptContent;

    //@NotNull(message = "강의 설명 파일은 필수 항목입니다.")
    private List<MultipartFile> courseDescriptFiles;

    @NotNull(message = "강의 상태는 필수 항목입니다.")
    private CourseStatus courseStatus; // 기본값: 대기

    @NotNull(message = "강의 세부 정보는 필수 항목입니다.")
    @Valid
    private List<CourseDetailRequest> courseDetails = new ArrayList<>();

    @Builder
    public CourseCreateRequest(String courseTitle, String courseDescript, Long coursePrice, String courseCategory, CourseDifficulty courseDifficulty, MultipartFile courseThumbnail, String courseDescriptContent, List<MultipartFile> courseDescriptFiles, CourseStatus courseStatus, List<CourseDetailRequest> courseDetails) {
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

    public CourseCreateServiceRequest toCourseServiceRequest(InstrEntity instrEntity) {
        return CourseCreateServiceRequest.builder()
                .instr(instrEntity)
                .courseThumbnail(courseThumbnail)
                .courseTitle(courseTitle)
                .courseDescript(courseDescript)
                .coursePrice(coursePrice)
                .courseCategory(courseCategory)
                .courseDifficulty(courseDifficulty)
                .courseStatus(courseStatus)
                .build();
    }

    public CourseDescriptCreateServiceRequest toCourseDescriptServiceRequest(CourseEntity courseEntity) {
        return CourseDescriptCreateServiceRequest.builder()
                .courseEntity(courseEntity)
                .courseDescriptContent(courseDescriptContent)
                .courseDescriptFiles(courseDescriptFiles)
                .build();
    }

    // 변환 메서드: CourseDetailRequest -> CourseDetailCreateServiceRequest
    public List<CourseDetailCreateServiceRequest> toCourseDetailServiceRequests(CourseEntity courseEntity) {

        return courseDetails.stream()
                .map(detail -> CourseDetailCreateServiceRequest.builder()
                        .courseEntity(courseEntity)
                        .courseDetailOutline(detail.getCourseDetailOutline())
                        .courseDetailTitle(detail.getCourseDetailTitle())
                        .courseDetailContent(detail.getCourseDetailContent())
                        .courseDetailFile(detail.getCourseDetailFile())
                        .videoUrl(detail.getVideoUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // Nested DTO for Course Details
//    @Getter
//    @NoArgsConstructor
//    public static class CourseDetailRequest {
//
//        @NotBlank(message = "상세 목차는 필수 항목입니다.")
//        private String courseDetailOutline;
//
//        @NotBlank(message = "세부 제목은 필수 항목입니다.")
//        private String courseDetailTitle;
//
//        private String courseDetailContent;
//
//        @NotNull(message = "강의 세부 파일은 필수 항목입니다.")
//        private MultipartFile courseDetailFile;
//    }
}
