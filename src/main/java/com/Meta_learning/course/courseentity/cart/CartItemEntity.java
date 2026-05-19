package com.Meta_learning.course.courseentity.cart;

import com.Meta_learning.course.courseentity.CourseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id") // 카트 아이템 ID
    private Long cartItemId;

    @CreatedDate
    @Column(name = "course_created_at", updatable = false) // 생성 날짜
    private LocalDateTime cartItemCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private CourseEntity course; // 코스 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", nullable = false)
    private CartEntity cart; // 카트 참조

    // 양방향 참조를 위한 setter
    public void setCart(CartEntity cart) {
        this.cart = cart;
    }

}
