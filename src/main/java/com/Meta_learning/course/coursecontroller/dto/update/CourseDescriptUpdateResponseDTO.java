package com.Meta_learning.course.coursecontroller.dto.update;

import com.Meta_learning.course.coursecontroller.dto.response.CourseDescriptFileUpdateResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseDescriptUpdateResponseDTO {
    private Long courseDescriptId;
    private String courseDescriptContent;
    private List<CourseDescriptFileUpdateResponse> courseDescriptFiles;

    @Builder
    public CourseDescriptUpdateResponseDTO(Long courseDescriptId, String courseDescriptContent, List<CourseDescriptFileUpdateResponse> courseDescriptFiles) {
        this.courseDescriptId = courseDescriptId;
        this.courseDescriptContent = courseDescriptContent;
        this.courseDescriptFiles = courseDescriptFiles;
    }
}
