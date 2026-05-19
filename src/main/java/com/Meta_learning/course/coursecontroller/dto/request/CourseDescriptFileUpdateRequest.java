package com.Meta_learning.course.coursecontroller.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDescriptFileUpdateRequest {
    private Long courseDetailFileId;
    private String courseDescriptFiles;
}
