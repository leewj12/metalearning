package com.Meta_learning.course.coursecontroller;

import com.Meta_learning.course.coursecontroller.dto.response.CartItemResponse;
import com.Meta_learning.course.courseservice.cart.CartService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CartRestController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/api/user/cart/items/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        List<CartItemResponse> cartItems = cartService.getCartItems(user);
//        if (cartItems.isEmpty()) {
//            return ResponseEntity.noContent().build(); // 204 No Content
//        }
        // 빈 리스트를 반환하여 프론트엔드에서 JSON 파싱 문제를 방지
        return ResponseEntity.ok(cartItems); // 200 OK
    }

    /**
     * 장바구니에 강의를 추가하는 API
     * @param user 인증된 사용자 정보
     * @param courseId 추가하려는 강의의 ID
     * @return 성공 또는 실패 메시지
     */
    @PostMapping("/api/user/cart/add/{courseId}")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal UserEntity user,
                                            @PathVariable Long courseId) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }


        try {
            cartService.addItemToCart(user, courseId);
            return ResponseEntity.ok("장바구니에 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("api/user/cart/remove/{courseId}")
    public ResponseEntity<String> deleteItemFromCart(@AuthenticationPrincipal UserEntity user, @PathVariable Long courseId) {
        try {
            cartService.deleteItemFromCart(user, courseId);
            return ResponseEntity.ok("장바구니에 담긴 강의가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("api/user/getUserInfo/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        // UserEntity 직접 반환
        UserEntity user = userRepository.findByUserId(userId);

        // 유저가 null인지 체크
        if (user == null) {
            // 유저를 찾을 수 없는 경우 404 응답 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found");
        }
        // 유저 정보를 Map에 담아 응답
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getUserEmail());
        userInfo.put("phone_number", user.getUserPhone());

        // 200 응답과 함께 유저 정보 반환
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/api/user/session")
    public ResponseEntity<Map<String, Object>> checkSession(@AuthenticationPrincipal UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("isAuthenticated", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // JSON 응답 반환
        }
        response.put("isAuthenticated", true);
        response.put("username", user.getUsername());
        return ResponseEntity.ok(response);
    }
}
