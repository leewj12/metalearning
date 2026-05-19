package com.Meta_learning.KDT.KDTDTO.KDTAttDTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTAttDTO {

    private Long kdtAttId;
    private Long kdtPartId;    //국비 참가자 id
    private LocalDate kdtAttDate;           //출석부 일자
    private LocalDateTime kdtAttEntryTime;  //출석부 입실 시간
    private LocalDateTime kdtAttExitTime;   //출석부 퇴실 시간
    private LocalDateTime kdtAttLeaveStart; //출석부 외출 시작 시간
    private LocalDateTime kdtAttLeaveEnd;   //출석부 외출 종료 시간
    private String kdtAttStatus;      //출석부 상태 예시: 입실, 퇴실, 외출 등

}
