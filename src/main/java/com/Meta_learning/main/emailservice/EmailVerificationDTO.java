package com.Meta_learning.main.emailservice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDTO {
    private String userEmail;         // 이메일
    private String verificationCode;  // 사용자가 입력한 인증 코드
}
