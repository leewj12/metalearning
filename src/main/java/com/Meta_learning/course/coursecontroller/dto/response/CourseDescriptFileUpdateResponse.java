package com.Meta_learning.course.coursecontroller.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDescriptFileUpdateResponse {
    private Long courseDescriptFileId;
    private String courseDescriptFiles;
}
