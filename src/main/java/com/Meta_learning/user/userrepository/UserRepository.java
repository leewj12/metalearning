package com.Meta_learning.user.userrepository;


import com.Meta_learning.user.userentity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserEmail(String userEmail);

    // userRole이 'MANAGER'인 사용자만 찾는 메서드 추가
    List<UserEntity> findByUserRole(String userRole);

    // UserId로 객체를 찾는 메서드
    UserEntity findByUserId(Long userId);
//
//    // 'STUDENT' 역할을 가진 사용자만 찾기 (페이징 포함)
//    Page<UserEntity> findByUserRole(String userRole, Pageable pageable);
//
//    // 'STUDENT' 역할을 가진 사용자만 찾고 이름에 대해 LIKE 검색 (페이징 포함)
//    Page<UserEntity> findByUserRoleAndNameContainingIgnoreCase(String userRole, String name, Pageable pageable);

    // 세션에 참여한 유저를 제외하고 STUDENT 역할을 가진 유저를 조회
    @Query("SELECT u FROM UserEntity u WHERE u.userRole = 'STUDENT' AND u.userId NOT IN " +
            "(SELECT p.userEntity.userId FROM KDTPartEntity p WHERE p.kdtSessionEntity.kdtSessionId = :sessionId)")
    Page<UserEntity> findByUserRoleAndUserIdNotIn(@Param("sessionId") Long sessionId, Pageable pageable);

    // 이름으로 검색하면서 세션에 참여한 유저 제외, 학생만 조회
    @Query("SELECT u FROM UserEntity u WHERE u.userRole = 'STUDENT' AND u.name LIKE %:searchName% AND u.userId NOT IN " +
            "(SELECT p.userEntity.userId FROM KDTPartEntity p WHERE p.kdtSessionEntity.kdtSessionId = :sessionId)")
    Page<UserEntity> findByUserRoleAndNameContainingIgnoreCaseAndUserIdNotIn(@Param("searchName") String searchName,
                                                                             @Param("sessionId") Long sessionId,
                                                                             Pageable pageable);


    // 이름, 생년월일, 연락처로 사용자 검색
    UserEntity findByNameAndUserBirthAndUserPhone(String name, LocalDate userBirth, String userPhone);

    // 'ADMIN' 권한을 제외한 모든 사용자 조회
    List<UserEntity> findByUserRoleNot(String userRole);


    // 권한별 사용자 수를 카운트하는 메서드들
    long countByUserRole(String userRole);


    // 권한이 '학생'인 사용자만 카운트
    @Query("SELECT FUNCTION('YEAR', u.userCreatedAt) AS year, " +
            "FUNCTION('MONTH', u.userCreatedAt) AS month, " +
            "COUNT(u) AS userCount " +
            "FROM UserEntity u " +
            "WHERE u.userRole = 'STUDENT' " + // 'STUDENT' 권한만 카운트
            "GROUP BY FUNCTION('YEAR', u.userCreatedAt), FUNCTION('MONTH', u.userCreatedAt) " +
            "ORDER BY year, month")
    List<Object[]> countStudentsByYearAndMonth();

//기존에 String을 사용한 방식 대신 LocalDate를 사용하도록 수정
    Optional<UserEntity> findByUserEmailAndNameAndUserBirth(String email, String name, LocalDate userBirth);

}
