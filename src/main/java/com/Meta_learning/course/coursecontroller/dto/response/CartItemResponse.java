package com.Meta_learning.course.coursecontroller.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartItemResponse {
    private Long courseId;
    private String courseTitle;
    private String courseThumbnail;
    private String courseDescript;
    private String instructorName;
    private Long coursePrice;
}
