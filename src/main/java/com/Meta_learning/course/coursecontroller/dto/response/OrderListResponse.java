package com.Meta_learning.course.coursecontroller.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderListResponse {
    private Long courseId; // 강의 ID
    private String courseTitle; // 강의 제목
    private Long orderTotalPrice; // 주문 총 가격
    private String thumbnailUrl;   // 썸네일 URL (추가)
}
