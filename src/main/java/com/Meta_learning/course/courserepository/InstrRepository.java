package com.Meta_learning.course.courserepository;

import com.Meta_learning.course.courseentity.InstrEntity;
import com.Meta_learning.course.courseentity.InstrStatus;
import com.Meta_learning.user.userentity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrRepository extends JpaRepository<InstrEntity, Long> {
    // userId로 InstrEntity 조회
    Optional<InstrEntity> findByUserEntityUserId(Long userId);

    boolean existsByUserEntity(UserEntity user);

    List<InstrEntity> findAllByInstrStatus(InstrStatus status);

    Optional<InstrEntity> findByUserEntity_UserEmail(String email);

    // userId로 InstrEntity가 존재하는지 확인하는 메서드
    boolean existsByUserEntity_UserId(Long userId);
}
