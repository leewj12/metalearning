package com.Meta_learning.user.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserPassWordDTO {
    private Long userId; // 유저 프라이머리 키
    private String userPw; // 유저 비밀번호
    private String newPw;  // 새 비밀번호
}
