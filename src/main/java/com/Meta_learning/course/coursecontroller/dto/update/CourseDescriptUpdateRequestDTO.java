package com.Meta_learning.course.coursecontroller.dto.update;

import com.Meta_learning.course.coursecontroller.dto.response.CourseDescriptFileUpdateResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseDescriptUpdateRequestDTO {
    private Long courseDescriptId;
    private String courseDescriptContent;
    private List<Long> filesToDelete; // 삭제할 파일 ID 목록
    private List<MultipartFile> courseDescriptFiles;

    @Builder
    public CourseDescriptUpdateRequestDTO(Long courseDescriptId, String courseDescriptContent, List<Long> filesToDelete, List<MultipartFile> courseDescriptFiles) {
        this.courseDescriptId = courseDescriptId;
        this.courseDescriptContent = courseDescriptContent;
        this.filesToDelete = filesToDelete;
        this.courseDescriptFiles = courseDescriptFiles;
    }
}
