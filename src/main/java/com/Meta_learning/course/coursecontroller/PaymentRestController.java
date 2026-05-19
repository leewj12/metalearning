package com.Meta_learning.course.coursecontroller;

import com.Meta_learning.course.coursecontroller.dto.request.PaymentRequest;
import com.Meta_learning.course.coursecontroller.dto.response.PayListResponse;
import com.Meta_learning.course.courseservice.cart.CartService;
import com.Meta_learning.course.courseservice.pay.PaymentService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PaymentRestController {

    private final CartService cartService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    // 결제 페이지를 생성하는 엔드포인트
    /*@GetMapping("/payment/start/{userId}")
    public ResponseEntity<Map<String, Object>> startPayment(
            @PathVariable Long userId,
            @RequestParam String courseIds,
            @RequestParam String courseTitles) {

        // 요청 데이터 확인
        System.out.println("User ID: " + userId);
        System.out.println("Course IDs: " + courseIds);
        System.out.println("Course Titles: " + courseTitles);

        // 예시로 반환할 결제 관련 데이터
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("userId", userId);
        paymentData.put("courseIds", Arrays.asList(courseIds.split(",")));
        paymentData.put("courseTitles", Arrays.asList(courseTitles.split(",")));
        paymentData.put("totalAmount", calculateTotalAmount(courseIds)); // 총 금액 계산

        return ResponseEntity.ok(paymentData);
    }*/

    //    @GetMapping("/payment/start/{userId}")
//    public String startPayment(@PathVariable Long userId,
//                               @RequestParam String courseIds,
//                               @RequestParam String courseTitles,
//                               Model model) {
//        // 요청 데이터 확인
//        System.out.println("User ID: " + userId);
//        System.out.println("Course IDs: " + courseIds);
//        System.out.println("Course Titles: " + courseTitles);
//
//        // 결제 정보 모델에 추가
//        model.addAttribute("userId", userId);
//        model.addAttribute("courseIds", courseIds);
//        model.addAttribute("courseTitles", courseTitles);
//
//  /*      // 결제 정보 가져오기
//        List<PaymentInfo> paymentInfoList = paymentService.getPaymentInfo(userId, courseIds);
//
//        // 결제 정보가 없을 경우 예외 처리
//        if (paymentInfoList.isEmpty()) {
//            throw new IllegalArgumentException("결제 정보가 없습니다.");
//        }
//
//        model.addAttribute("paymentInfoList", paymentInfoList);*/
//
//        // 결제 페이지 템플릿 반환
//        return "users/paymentpage";
//    }
    // 강의 ID를 기준으로 총 금액 계산
    private int calculateTotalAmount(String courseIds) {
        // 예시로 강의 하나당 10,000원으로 설정
        int pricePerCourse = 10000;

        // 쉼표로 구분된 courseIds를 배열로 변환 후, 강의 수에 따라 총 금액 계산
        String[] ids = courseIds.split(",");
        return ids.length * pricePerCourse;
    }

    @PostMapping("/payment/complete")
    public ResponseEntity<String> completePayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            paymentService.processPayment(paymentRequest);

            UserEntity user = userRepository.findByUserId(paymentRequest.getUserId());
            if (user == null) {
                throw new IllegalArgumentException("해당 사용자(ID: " + paymentRequest.getUserId() + ")를 찾을 수 없습니다.");
            }

            paymentRequest.getItems().forEach(item ->
                    cartService.deleteItemFromCart(user, item.getCourseId())
            );

            return ResponseEntity.ok("결제가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            // 예외 발생 시 상세 로그 출력
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/api/admin/paylist")
    public ResponseEntity<List<PayListResponse>> getPayList() {
        List<PayListResponse> payList = paymentService.getAllPayList();
        return ResponseEntity.ok(payList);
    }

    @PostMapping("/api/admin/paylist/refund/{payId}")
    public ResponseEntity<String> processRefund(@PathVariable Long payId) {
        try {
            paymentService.refundPayment(payId); // 환불 비즈니스 로직 실행
            return ResponseEntity.ok("환불 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 실패: " + e.getMessage());
        }
    }


//    @PostMapping("/payment/complete")
//    public ResponseEntity<String> completePay(@RequestBody Map<String, Object> paymentData) {
//        // 결제 완료 후 포트원에서 받은 데이터를 확인
//        System.out.println("결제 데이터: " + paymentData);
//
//        // TODO: 결제 데이터 검증 및 처리
//        String impUid = (String) paymentData.get("imp_uid"); // 포트원 거래 고유번호
//        String merchantUid = (String) paymentData.get("merchant_uid"); // 주문번호
//
//        // 검증 및 결제 데이터 처리 로직 추가
//        // 예: 데이터베이스 저장, 상태 업데이트 등
//
//        return ResponseEntity.ok("결제가 완료되었습니다.");
//    }


//    @PostMapping("/complete")
//    public ResponseEntity<String> completePayment(@RequestBody OrderRequest orderRequest) {
//        // 요청 데이터 검증
//        if (orderRequest == null || orderRequest.getItems().isEmpty()) {
//            return ResponseEntity.badRequest().body("결제 항목이 없습니다.");
//        }
//
//        // 총 결제 금액 계산
//        long totalCalculatedPrice = orderRequest.getItems().stream()
//                .mapToLong(OrderItem::getCoursePrice)
//                .sum();
//
//        if (totalCalculatedPrice != orderRequest.getTotalPrice()) {
//            return ResponseEntity.badRequest().body("결제 금액이 일치하지 않습니다.");
//        }
//
//        // 결제 처리 로직 추가 (DB 저장 등)
//        paymentService.processOrder(orderRequest);
//
//        return ResponseEntity.ok("결제가 성공적으로 완료되었습니다.");
//    }
}
