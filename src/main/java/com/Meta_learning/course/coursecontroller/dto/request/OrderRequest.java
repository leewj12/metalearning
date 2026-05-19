package com.Meta_learning.course.coursecontroller.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId; // 사용자 ID
    private List<OrderItem> items; // 결제 항목
    private Long totalPrice; // 총 결제 금액

    @Getter
    @Setter
    public static class OrderItem {
        private Long courseId; // 강의 ID
        private String courseTitle; // 강의 제목
        private Long coursePrice; // 강의 가격
    }
}