package com.Meta_learning.user.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FindUserIdDTO {
    private String name;
    private LocalDate userBirth;
    private String userPhone;

    // 날짜 파싱 처리
    public void setUserBirth(String userBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.userBirth = LocalDate.parse(userBirth, formatter);
    }

    // Getters and setters
}
