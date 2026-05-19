package com.Meta_learning.user.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정을 위한 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {

    private String name;      // 사용자 이름
    private String birth;     // 사용자 생년월일 (yyyy-MM-dd 형식)
    private String email;     // 사용자 이메일
}
