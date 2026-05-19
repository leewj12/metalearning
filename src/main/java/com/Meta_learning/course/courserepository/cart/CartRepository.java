package com.Meta_learning.course.courserepository.cart;

import com.Meta_learning.course.courseentity.cart.CartEntity;
import com.Meta_learning.user.userentity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserEntity(UserEntity user);
}
