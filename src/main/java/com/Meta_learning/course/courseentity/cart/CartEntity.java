package com.Meta_learning.course.courseentity.cart;

import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id") // 카트 ID
    private Long cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity; // 유저 엔티티 참조 (유저 ID)

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> cartItems = new ArrayList<>(); // 카트 아이템 리스트

    // 카트에 아이템 추가 메서드 (양방향 연결)
    public void addItem(CartItemEntity cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    // 카트에서 아이템 제거 메서드
    public void removeItem(CartItemEntity cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
    }
}
