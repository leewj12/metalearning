package com.Meta_learning.admin.admindashboarddto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class UserStatusDTO {

    private String userEmail;  // 사용자 이메일 (유니크)
    private String userRole;  // 사용자 역할 (ex: USER, ADMIN)
    private String name;  // 사용자 이름

}
