package com.Meta_learning.admin.admindashboarddto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserCountDTO {

    private int year;  // 년도
    private int month; // 월
    private long userCount; // 해당 월의 학생 수
}
