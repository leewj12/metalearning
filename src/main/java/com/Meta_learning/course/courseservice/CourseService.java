package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.response.CourseViewResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseUpdateRequestDTO;
import com.Meta_learning.course.coursecontroller.dto.update.CourseUpdateResponseDTO;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.CourseStatus;
import com.Meta_learning.course.courseentity.InstrEntity;
import com.Meta_learning.course.courseservice.requset.CourseCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;

import java.util.List;
import java.util.Map;

public interface CourseService {
    CourseEntity saveCourse(CourseCreateServiceRequest courseServiceRequest);

    CourseEntity getCourseById(Long courseId);

    CourseEntity updateCourse(CourseUpdateRequestDTO request);

//    MultipartFile convertFileToMultipart(String filePath);

    List<CourseEntity> getCoursesByInstructor(InstrEntity instrEntity);

    List<CourseEntity> getApprovedCourses();

    Long getCourseCount(UserEntity user);

    List<Map<String, Object>> getMonthlyUploadCourseCount();

    List<CourseEntity> getPendingCourses();

    void updateCourseStatus(Long courseId, CourseStatus status);

    void deleteCourseById(Long courseId);

    void deleteCourseVideoById(Long courseVideoId);

    CourseViewResponse getCourseViewByDetailId(Long courseDetailId);
}
