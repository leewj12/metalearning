package com.Meta_learning.course.coursecontroller;

import com.Meta_learning.course.coursecontroller.dto.response.CartItemResponse;
import com.Meta_learning.course.courseservice.cart.CartService;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/cart")
    public String cart(@AuthenticationPrincipal UserEntity user,
                       Model model) {
        List<CartItemResponse> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);
        return "users/cart"; // 뷰 반환
    }




}
