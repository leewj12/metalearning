package com.Meta_learning.course.coursecontroller.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemDto {
    private Long courseId; // 강의 ID
    private String courseTitle; // 강의 제목
    private Long coursePrice; // 강의 가격
}