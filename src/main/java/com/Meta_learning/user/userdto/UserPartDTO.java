package com.Meta_learning.user.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserPartDTO {
    private Long userId; // 유저 프라이머리 키
    private Long kdtPartId;             // KDTPart ID
    private Long kdtSessionId;          // 국비회차 ID
    private String userEmail; // 유저 이메일 = 실제 사용 아이디
    private String userRole; // 유저 권환
    private String name; // 유저 이름
    private String userGender; // 유저 성별
    private LocalDate userBirth; // 유저 생년월일
    private String userPhone; // 유저 연락처
    private String userAddress; // 유저 주소
    private String userEduLevel; // 유저 최종학력
    private String kdtPartStatus;       // 수업 상태 (대기, 수료중 등) -> String
    private Boolean kdtPartEmp;         // 취업 상태 (True/False)
}
