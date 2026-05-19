package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.response.CourseDescriptFileUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseDescriptUpdateRequestDTO;
import com.Meta_learning.course.courseentity.CourseDescriptEntity;
import com.Meta_learning.course.courseentity.CourseDescriptFileEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseservice.requset.CourseDescriptCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDescriptUpdateServiceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseDescriptService {
    CourseDescriptEntity saveCourseDescript(CourseDescriptCreateServiceRequest courseDescriptServiceRequest);

    CourseDescriptEntity getCourseDescript(CourseEntity courseEntity);

    List<CourseDescriptFileEntity> getCourseDescriptFiles(CourseDescriptEntity courseDescript);

    List<CourseDescriptFileUpdateResponse> convertDescriptFilesToFileNames(List<CourseDescriptFileEntity> descriptFileEntities);

//    MultipartFile convertFileToMultipart(String filePath);
//
//    List<MultipartFile> convertDescriptFilesToMultipart(List<CourseDescriptFileEntity> descriptFileEntities);

    CourseDescriptEntity updateCourseDescript(CourseDescriptUpdateRequestDTO request);
}
