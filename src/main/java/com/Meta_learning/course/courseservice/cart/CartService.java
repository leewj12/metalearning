package com.Meta_learning.course.courseservice.cart;

import com.Meta_learning.course.coursecontroller.dto.response.CartItemResponse;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.cart.CartEntity;
import com.Meta_learning.course.courseentity.cart.CartItemEntity;
import com.Meta_learning.course.courserepository.CourseRepository;
import com.Meta_learning.course.courserepository.cart.CartItemRepository;
import com.Meta_learning.course.courserepository.cart.CartRepository;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;

    public List<CartItemResponse> getCartItems(UserEntity user) {
        // 유저의 카트와 카트 아이템 가져오기
        CartEntity cart = getOrCreateCart(user);
        // 카트와 카트 아이템을 모델에 추가
        return cart.getCartItems().stream()
                .map(item -> new CartItemResponse(
                        item.getCourse().getCourseId(),
                        item.getCourse().getCourseTitle(),
                        item.getCourse().getCourseThumbnail(),
                        item.getCourse().getCourseDescript(),
                        item.getCourse().getInstr().getUserEntity().getName(),
                        item.getCourse().getCoursePrice()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteItemFromCart(UserEntity user, Long courseId) {
        // 카트를 가져오기
        CartEntity cart = cartRepository.findByUserEntity(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 장바구니가 없습니다."));

        // 삭제할 아이템 찾기
        CartItemEntity cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCourse().getCourseId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 강의가 없습니다."));

        // 장바구니에서 아이템 제거
        cart.removeItem(cartItem); // 필요하면 CartEntity에 removeCartItem 메서드를 구현
        cartItemRepository.delete(cartItem); // DB에서 아이템 삭제
    }

    // 유저의 카트 가져오기, 없으면 생성
    public CartEntity getOrCreateCart(UserEntity user) {
        // 해당 유저의 카트를 조회
        return cartRepository.findByUserEntity(user)
                .orElseGet(() -> createCartForUser(user));
    }

    // 새로운 카트 생성
    private CartEntity createCartForUser(UserEntity user) {
        // CartEntity 생성 및 저장
        CartEntity newCart = CartEntity.builder()
                .userEntity(user)
                .build();

        return cartRepository.save(newCart);
    }

    // 카트에 아이템 추가
    public void addItemToCart(UserEntity user, Long courseId) {
        // 유저의 카트를 가져오거나 생성
        CartEntity cart = getOrCreateCart(user);

        // 이미 해당 강의가 카트에 있는지 확인
        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(cartItem -> cartItem.getCourse().getCourseId().equals(courseId));
        if (itemExists) {
            throw new IllegalArgumentException("이미 장바구니에 추가된 강의입니다.");
        }

        // 강의 정보 가져오기
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        // 새로운 CartItem 생성 및 추가
        CartItemEntity newItem = CartItemEntity.builder()
                .course(course)
                .cart(cart)
                .build();

        // 카트에 아이템 추가 (양방향 매핑을 보장)
        cart.addItem(newItem);

        // 변경 사항 저장
        cartItemRepository.save(newItem); // DB에 저장
    }
}
