package com.Meta_learning.admin.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InstrCreateResponse {
    private String name;  // 사용자 이름
    private String userEmail;  // 사용자 이메일 (유니크)
    private String instrDescript;
    private String instrCompany;
}
