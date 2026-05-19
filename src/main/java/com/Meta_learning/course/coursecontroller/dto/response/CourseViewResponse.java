package com.Meta_learning.course.coursecontroller.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseViewResponse {

 private String courseDetailTitle;
 private String courseVideoType;
 private String courseVideoUuid;


}