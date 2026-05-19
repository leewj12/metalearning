package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.request.CourseDetailUpdateRequest;
import com.Meta_learning.course.coursecontroller.dto.response.CourseDetailUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseDetailUpdateRequestDTO;
import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.CourseFileEntity;
import com.Meta_learning.course.courseentity.CourseVideoEntity;
import com.Meta_learning.course.courseservice.requset.CourseDetailCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDetailUpdateServiceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseDetailService {
    void saveCourseDetails(List<CourseDetailCreateServiceRequest> requests);

    List<CourseDetailEntity> getCourseDetails(CourseEntity courseEntity);

    List<CourseVideoEntity> getCourseVideos(List<CourseDetailEntity> courseDetails);

    List<CourseFileEntity> getCourseFiles(List<CourseDetailEntity> courseDetails);

    List<CourseDetailUpdateResponse> convertCourseDetailsToUpdateResponses(List<CourseDetailEntity> courseDetailEntities);

    void updateCourseDetailFile(CourseDetailUpdateRequestDTO request);

    void updateCourseDetailVideo(CourseDetailUpdateRequestDTO request);

    void updateCourseDetails(List<CourseDetailUpdateServiceRequest> serviceRequests);
}
