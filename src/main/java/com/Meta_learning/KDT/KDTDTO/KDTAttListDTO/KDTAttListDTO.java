package com.Meta_learning.KDT.KDTDTO.KDTAttListDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTAttListDTO {
    // 오늘 날짜로 목록을 볼 예정
    private Long kdtPartId;                 //참석자 id
    private Long userId;                    //유저 id
    private String kdtPartName;             //참가 학생 이름
    private String kdtAttStatus;            //상태
    private double kdtAttRate;              //출석율
    private int attCount;                   //출석횟수
    private int tardyCount;                 //지각 횟수
    private int earlyLeaveCount;            //조퇴횟수
    private int outgoingCount;              //외출횟수
    private int absenceCount;               //결석 횟수
    private LocalDateTime kdtAttEntryTime;  //출석부 입실 시간
    private LocalDateTime kdtAttExitTime;   //출석부 퇴실 시간
}