package com.Meta_learning.KDT.KDTrepository.KDTStaffRepository;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.user.userentity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KDTStaffRepository extends JpaRepository<KDTStaffEntity,Long> {
    // 세션과 사용자 조합이 이미 존재하는지 확인하는 메서드
    boolean existsByKdtSessionEntityAndUserEntity(KDTSessionEntity sessionEntity, UserEntity userEntity);

    // 세션 ID로 이미 등록된 강사들을 조회하는 메서드
    List<KDTStaffEntity> findByKdtSessionEntity_KdtSessionId(Long sessionId);

    // 세션 ID와 사용자 ID로 강사를 찾는 메서드
    Optional<KDTStaffEntity> findByUserEntityUserIdAndKdtSessionEntityKdtSessionId(Long userId, Long kdtSessionId);

    // 세션 ID와 유저 Role로 해당하는 Role을 조회하는 메서드
    List<KDTStaffEntity> findByKdtSessionEntity_KdtSessionIdAndUserEntity_userRole(Long sessionId, String role);

    // staff ID로 객체를 찾는 메서드
    KDTStaffEntity findByKdtStaffId(Long kdtStaffId);


    // 사용자 이메일과 세션 ID로 매니저 여부 확인
    boolean existsByUserEntity_UserEmailAndKdtSessionEntity_KdtSessionId(String userEmail, Long sessionId);

    // 해당 userId로 담당하는 세션 찾기
    List<KDTStaffEntity> findByUserEntity_UserId(Long userId);

    // 특정 사용자 이메일과 국비 과정 ID로 해당 사용자가 담당하는 세션이 있는지 확인
    boolean existsByUserEntity_UserEmailAndKdtSessionEntity_KdtCourseEntity_KdtCourseId(String userEmail, Long kdtCourseId);

    // 특정 유저와 특정 세션에 배정된 스태프가 있는지 확인
    boolean existsByUserEntity_UserIdAndKdtSessionEntity_KdtSessionId(Long userId, Long sessionId);


    // userId와 courseId에 해당하는 세션 목록을 반환하는 쿼리 메서드
    List<KDTSessionEntity> findByUserEntity_UserIdAndKdtSessionEntity_KdtCourseEntity_KdtCourseId(Long userId, Long courseId);

    // userId로 staff와 관련된 모든 정보를 가져오는 퀴리 메서드
    List<KDTStaffEntity> findByUserEntityUserId(Long userId);
}
