package com.Meta_learning.user.userdto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSignUpDTO {
    private Long userId; // 유저 프라이머리 키
    private String userEmail; // 유저 이메일 = 실제 사용 아이디
    private String userPw; //유저 비밀번호
    private String userRole; // 유저 권환
    private String name; // 유저 이름
    private String userGender; // 유저 성별
    private LocalDate userBirth; // 유저 생년월일
    private String userPhone; // 유저 연락처
    private String userPostcode; // 유저 우편주소
    private String userAddress; // 유저 주소
    private String userAddressDetail; // 유저 상세주소
    private String userEduLevel; // 유저 최종학력
    private Boolean userMarketingAgree;  // 유저 마케팅 동의
    private Boolean userPrivacyAgree;    // 유저 개인정보동의

    private String userStatus;       // 유저 상태
    private LocalDateTime userCreatedAt; // 유저 생성일

    private LocalDateTime userUpdatedAt; // 유저 업데이트일
    private String userSns; // 유저 소셜로그인 파악
    private String userThumbnail; // 유저 이미지
    private LocalDateTime userLastLogin; // 유저 마지막 로그인

    // 날짜를 포맷팅하는 메서드
    public String formatUserCreatedAt() {
        return formatDateTime(userCreatedAt);
    }

    public String formatUserUpdatedAt() {
        return formatDateTime(userUpdatedAt);
    }

    public String formatUserLastLogin() {
        return formatDateTime(userLastLogin);
    }




    // 공통 포맷 메서드
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A"; // null 값 처리
        }
        // yyyy년 MM월 dd일 hh:mm a 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm");
        return dateTime.format(formatter);
    }



}
