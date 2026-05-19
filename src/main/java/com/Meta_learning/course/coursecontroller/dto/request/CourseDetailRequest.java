package com.Meta_learning.course.coursecontroller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CourseDetailRequest {

    //@NotBlank(message = "상세 목차는 필수 항목입니다.")
    private String courseDetailOutline;

    @NotBlank(message = "세부 제목은 필수 항목입니다.")
    private String courseDetailTitle;

    private String courseDetailContent;

//    @NotNull(message = "강의 세부 파일은 필수 항목입니다.")
    private MultipartFile courseDetailFile;

    private String videoUrl;
}
