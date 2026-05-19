package com.Meta_learning.course.coursecontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PaymentController {

//    @GetMapping("/payment/start/{userId}")
//    public String startPayment(@PathVariable Long userId,
//                               @RequestParam String courseIds,
//                               @RequestParam String courseTitles,
//                               Model model) {
//        // Mock 데이터로 사용자의 정보를 가져오기 (예: 데이터베이스 또는 인증된 사용자 정보)
//        Map<String, String> user = new HashMap<>();
//        user.put("email", "test@example.com");  // 예제 데이터
//        user.put("name", "Test User");
//
//        // 필수 데이터 추가
//        model.addAttribute("user", user);  // Thymeleaf 템플릿에서 사용
//        model.addAttribute("courseIds", courseIds);
//        model.addAttribute("courseTitles", courseTitles);
//
//        return "/users/paymentpage";  // 올바른 뷰 이름 반환
//    }

    @GetMapping("/admin/pay/list")
    public String getPayList() {
        return "admin/paylist";
    }
}
