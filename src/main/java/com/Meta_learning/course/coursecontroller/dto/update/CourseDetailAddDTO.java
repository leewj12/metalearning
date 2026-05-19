package com.Meta_learning.course.coursecontroller.dto.update;

import com.Meta_learning.course.coursecontroller.dto.request.CourseDetailRequest;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseservice.requset.CourseDetailCreateServiceRequest;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailAddDTO {
    private List<CourseDetailRequest> courseDetails;

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

}
