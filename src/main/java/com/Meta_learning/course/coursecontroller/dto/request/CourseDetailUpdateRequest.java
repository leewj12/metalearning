package com.Meta_learning.course.coursecontroller.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailUpdateRequest {
    private Long courseDetailId;
    private String courseDetailOutline;
    private String courseDetailTitle;
    private String courseDetailContent;
    private MultipartFile courseDetailFile;
    private String videoUrl;
}
