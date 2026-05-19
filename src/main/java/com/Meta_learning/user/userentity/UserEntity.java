package com.Meta_learning.user.userentity;

import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * UserEntity 클래스는 사용자 정보와 관련된 엔티티로, 데이터베이스 테이블 'users'에 매핑됩니다.
 * 또한, Spring Security의 UserDetails 인터페이스를 구현하여 인증 및 권한 관련 기능을 제공합니다.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자는 보호된 접근자로 설정
@AllArgsConstructor
@Builder(toBuilder = true)  // Builder 사용 시 기존 값 복사 가능
@Getter
@EntityListeners(AuditingEntityListener.class)  // 생성일자와 수정일자를 자동으로 관리하는 리스너
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;  // 사용자 고유 ID

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;  // 사용자 이메일 (유니크)

    @Column(name = "user_pw", nullable = false)
    private String userPw;  // 사용자 비밀번호

    @Column(name = "user_role", nullable = false)
    private String userRole;  // 사용자 역할 (ex: USER, ADMIN)

    @Column(name = "name", nullable = false)
    private String name;  // 사용자 이름

    @Column(name = "user_gender", nullable = false)
    private String userGender;  // 사용자 성별

    @Column(name = "user_birth", nullable = false)
    private LocalDate userBirth;  // 사용자 생일

    @Column(name = "user_phone", nullable = false)
    private String userPhone;  // 사용자 전화번호

    @Column(name = "user_postcode")
    private String userPostcode;  // 사용자 우편번호

    @Column(name = "user_address")
    private String userAddress;  // 사용자 주소

    @Column(name = "user_address_detail")
    private String userAddressDetail;  // 상세 주소

    @Column(name = "user_edu_level")
    private String userEduLevel;  // 사용자 교육 수준

    @Column(name = "user_marketing_agree")
    private Boolean userMarketingAgree;  // 마케팅 정보 수신 동의 여부

    @Column(name = "user_privacy_agree", nullable = false)
    private Boolean userPrivacyAgree;  // 개인정보 처리방침 동의 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;  // 사용자 상태 (ACTIVE, INACTIVE 등)

    @CreatedDate
    @Column(name = "user_created_at", updatable = false)
    private LocalDateTime userCreatedAt;  // 사용자 계정 생성일자

//    @LastModifiedDate
    @Column(name = "user_updated_at")
    private LocalDateTime userUpdatedAt;  // 사용자 정보 수정일자

    @Column(name = "user_sns")
    private String userSns;  // SNS 계정 정보 (선택사항)

    @Column(name = "user_thumbnail")
    private String userThumbnail;  // 사용자 프로필 사진

    @Column(name = "user_last_login")
    private LocalDateTime userLastLogin;  // 마지막 로그인 시간

    public void upUserRoleInstr() {
        this.userRole = "INSTRUCTOR";
    }

    @Transactional
    public UserEntity withUserLastLogin(LocalDateTime newLoginTime) {
        return this.toBuilder()
                .userLastLogin(newLoginTime)  // userLastLogin만 수정
                .build();
    }

    /**
     * 사용자 권한 정보를 반환하는 메서드
     * @return 사용자 권한 (ROLE_로 시작하는 문자열)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + this.userRole));
        return authorities;
    }

    /**
     * 비밀번호를 반환하는 메서드
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return this.userPw;
    }

    /**
     * 사용자 이메일을 반환하는 메서드
     * @return 사용자 이메일
     */
    @Override
    public String getUsername() {
        return this.userEmail;
    }

    /**
     * 계정이 만료되었는지 확인하는 메서드
     * @return true (만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 항상 true로 설정 (만료되지 않음)
    }

    /**
     * 계정이 잠겨있는지 확인하는 메서드
     * @return true (잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;  // 항상 true로 설정 (잠기지 않음)
    }

    /**
     * 비밀번호가 만료되었는지 확인하는 메서드
     * @return true (만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 항상 true로 설정 (만료되지 않음)
    }

    /**
     * 계정이 활성화 되어 있는지 확인하는 메서드
     * @return true (활성화된 계정만 enabled 상태)
     */
    @Override
    public boolean isEnabled() {
        return this.userStatus == UserStatus.ACTIVE;  // 활성 상태일 때만 enabled
    }



    // 업데이트 메서드 일부만
    public void userUpdate(UserSignUpDTO userSignUpDTO) {
        // name이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getName() != null) {
            this.name = userSignUpDTO.getName();
        }

        // userRole이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserRole() != null) {
            this.userRole = userSignUpDTO.getUserRole();
        }

        // userGender가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserGender() != null) {
            this.userGender = userSignUpDTO.getUserGender();
        }

        // userBirth가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserBirth() != null) {
            this.userBirth = userSignUpDTO.getUserBirth();
        }

        // userPhone이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserPhone() != null) {
            this.userPhone = userSignUpDTO.getUserPhone();
        }

        // userPostcode가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserPostcode() != null) {
            this.userPostcode = userSignUpDTO.getUserPostcode();
        }

        // userAddress가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserAddress() != null) {
            this.userAddress = userSignUpDTO.getUserAddress();
        }

        // userAddressDetail이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserAddressDetail() != null) {
            this.userAddressDetail = userSignUpDTO.getUserAddressDetail();
        }

        // userEduLevel이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserEduLevel() != null) {
            this.userEduLevel = userSignUpDTO.getUserEduLevel();
        }

        // userMarketingAgree가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserMarketingAgree() != null) {
            this.userMarketingAgree = userSignUpDTO.getUserMarketingAgree();
        }

        // userPrivacyAgree가 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserPrivacyAgree() != null) {
            this.userPrivacyAgree = userSignUpDTO.getUserPrivacyAgree();
        }
        // userStatus가 null이 아닐 경우에만 업데이트
        // userStatus가 null이 아니고, UserStatus enum 값으로 변환할 수 있을 경우에만 업데이트
        if (userSignUpDTO.getUserStatus() != null) {
            try {
                // String 값으로 받은 상태를 UserStatus enum으로 변환
                this.userStatus = UserStatus.valueOf(userSignUpDTO.getUserStatus());
            } catch (IllegalArgumentException e) {
                // 잘못된 값이 들어온 경우 처리 (예: 유효하지 않은 상태 값)
            }
        }

        // userThumbnail이 null이 아닐 경우에만 업데이트
        // userThumbnail이 null이 아닐 경우에만 업데이트
        if (userSignUpDTO.getUserThumbnail() != null) {
            this.userThumbnail = userSignUpDTO.getUserThumbnail();
        }

        // userUpdatedAt을 현재 시간으로 업데이트
        this.userUpdatedAt = LocalDateTime.now();
    }


    //비밀번호 변경 메서드
    public void userPassWordUpdate(UserPassWordDTO userPassWordDTO) {
        // 새 비밀번호 가져오기
        String newPassword = userPassWordDTO.getNewPw();

        // 새 비밀번호가 null이 아니고, 비밀번호가 변경될 경우에만 처리
        if (newPassword != null && !newPassword.isEmpty()) {
            // 새 비밀번호를 암호화
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedNewPassword = passwordEncoder.encode(newPassword);

            // 비밀번호 업데이트
            this.userPw = encodedNewPassword;

            // 마지막 수정일자 업데이트
            this.userUpdatedAt = LocalDateTime.now();
        }
    }

    // userId와 userPw만 업데이트하는 메서드
    public void updateUserIdAndPassword(Long newUserId, String newPassword) {
        // userId가 null이 아니면 업데이트
        if (newUserId != null) {
            this.userId = newUserId;  // userId만 업데이트
        }

        // newPassword가 null이 아니고 비어 있지 않으면 비밀번호 업데이트
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            this.userPw = newPassword;  // 이미 해싱된 비밀번호를 그대로 저장
        }

        // 수정일시 업데이트 (현재 시간)
        this.userUpdatedAt = LocalDateTime.now();
    }



}
