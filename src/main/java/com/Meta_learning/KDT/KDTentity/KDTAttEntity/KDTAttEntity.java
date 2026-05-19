package com.Meta_learning.KDT.KDTentity.KDTAttEntity;


import com.Meta_learning.KDT.KDTDTO.KDTAttDTO.KDTAttDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "KDT_att")
@Getter
public class KDTAttEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_att_id")
    private Long kdtAttId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_part_id", referencedColumnName = "KDT_part_id", nullable = false)
    private KDTPartEntity kdtPartEntity;  // 국비 참가자 id

    @CreatedDate
    @Column(name = "KDT_att_date", nullable = false, updatable = false)
    private LocalDate kdtAttDate;   // 출석부 일자

    @Column(name = "KDT_att_entry_time")
    private LocalDateTime kdtAttEntryTime;  //출석부 입실 시간

    @Column(name = "KDT_att_exit_time")
    private LocalDateTime kdtAttExitTime;   //출석부 퇴실 시간

    @Column(name = "KDT_att_leave_start")
    private LocalDateTime kdtAttLeaveStart; //출석부 외출 시작 시간

    @Column(name = "KDT_att_leave_end")
    private LocalDateTime kdtAttLeaveEnd;   //출석부 외출 종료 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "KDT_att_status")
    private KDTAttStatus kdtAttStatus;      //출석부 상태 예시: 입실, 퇴실, 외출 등

    public void setStatus(LocalTime startTime, LocalTime endTime){
        if(kdtAttStatus != null && kdtAttStatus != KDTAttStatus.ERROR && kdtAttStatus != KDTAttStatus.ARRIVAL && kdtAttStatus != KDTAttStatus.OUTGOING) {
            return;  // "병결" 또는 "휴가" 상태인 경우 함수 종료
        }

        // 입실 시간이 없는데 다른 데이터 값이 있다면 error 반환
        if(kdtAttEntryTime == null &&(kdtAttExitTime != null || kdtAttLeaveStart != null || kdtAttLeaveEnd != null)) {
            kdtAttStatus = KDTAttStatus.valueOf("ERROR");
            return;
        }

        // 외출 시작은 있는데 외출 종료가 있다면 ERROR
        if(kdtAttLeaveStart == null && kdtAttLeaveEnd != null) {
            this.kdtAttStatus = KDTAttStatus.valueOf("ERROR");
            return;
        }
        if(kdtAttLeaveStart != null && kdtAttLeaveEnd == null && kdtAttExitTime !=null) {
            this.kdtAttStatus = KDTAttStatus.valueOf("ERROR");
            return;
        }

        // 출석부 일자의 날짜와 입실시간, 외출 시작 시간, 외출 종료시간의 날짜가 동일해야 함
        if(kdtAttEntryTime != null && !kdtAttDate.isEqual(kdtAttEntryTime.toLocalDate()) ||
                (kdtAttLeaveStart != null && !kdtAttDate.isEqual(kdtAttLeaveStart.toLocalDate())) ||
                (kdtAttLeaveEnd != null && !kdtAttDate.isEqual(kdtAttLeaveEnd.toLocalDate())) ||
                (kdtAttExitTime != null && !kdtAttDate.isEqual(kdtAttExitTime.toLocalDate()))) {
            this.kdtAttStatus = KDTAttStatus.valueOf("ERROR");
            return;  // 날짜가 다르면 "error" 리턴
        }
        // 입실 시간 < 외출 시작 시간 < 외출 종료 시간 < 퇴실 시간 (null 처리 포함)
        if((kdtAttEntryTime != null && kdtAttLeaveStart != null && kdtAttEntryTime.isAfter(kdtAttLeaveStart)) ||
                (kdtAttLeaveStart != null && kdtAttLeaveEnd != null && kdtAttLeaveStart.isAfter(kdtAttLeaveEnd)) ||
                (kdtAttLeaveEnd != null && kdtAttExitTime != null && kdtAttLeaveEnd.isAfter(kdtAttExitTime)) ||
                (kdtAttEntryTime != null && kdtAttExitTime != null && kdtAttEntryTime.isAfter(kdtAttExitTime))) {
            this.kdtAttStatus = KDTAttStatus.valueOf("ERROR");
            return;
        }

        if (kdtAttExitTime == null){
            // 퇴실 시간이 없으면 '입실'과 '외출' 일 수 있음
            // 외출 여부 판단
            if(kdtAttLeaveStart != null && kdtAttLeaveEnd == null){
                this.kdtAttStatus = KDTAttStatus.valueOf("OUTGOING");
                return;
            }

            // 현재 날짜와 시간이 기준시간(kdtAttDate pm 11시 55분) 이후라면 결석 처리
            LocalDateTime thresholdTime = LocalDateTime.of(kdtAttDate, LocalTime.of(23, 55));
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(thresholdTime)) {
                this.kdtAttStatus = KDTAttStatus.valueOf("ABSENT");
                return;
            }
            // 이 조건들이 아니라면 입실
            this.kdtAttStatus = KDTAttStatus.valueOf("ARRIVAL");
            return;
        }else {
            //퇴실 시간이 있으면 '출석', '조퇴', '결석'일 수 있음
            // 결석 여부 판단
            // 전체 수강 시간 계산 (퇴실 시간 - 입실시간 - 외출 시간)
            long totalMinutes = 0;

            // 입실 시간부터 퇴실 시간 or 세션 종료 시간 까지의 시간 계산
            LocalDateTime endDateTime = LocalDateTime.of(kdtAttDate, endTime);
            java.time.LocalDateTime selectedTime = endDateTime.isBefore(kdtAttExitTime) ? endDateTime : kdtAttExitTime;
            totalMinutes += java.time.Duration.between(kdtAttEntryTime, selectedTime).toMinutes();

            // 외출 시간이 있다면 외출 시간을 계산하여 추가
            if(kdtAttLeaveStart != null && kdtAttLeaveEnd != null) {
                totalMinutes -= java.time.Duration.between(kdtAttLeaveStart, kdtAttLeaveEnd).toMinutes();
            }

            // 전체 수강 시간을 계산하고 금일 수강 시간이 50% 미만이면 결석
            long totalSessionMinutes = java.time.Duration.between(LocalTime.of(startTime.getHour(), startTime.getMinute()), LocalTime.of(endTime.getHour(), endTime.getMinute())).toMinutes();
            if(totalMinutes < totalSessionMinutes / 2) {
                this.kdtAttStatus = KDTAttStatus.valueOf("ABSENT");
                return;  // 전체 시간의 50% 미만 출석이면 결석
            }

            // 출석 여부 판단
            LocalDateTime adjustedEndTime = LocalDateTime.of(kdtAttExitTime.toLocalDate(), endTime.minusMinutes(10));
            if(kdtAttExitTime.isAfter(adjustedEndTime)) {
                this.kdtAttStatus = KDTAttStatus.valueOf("DEPARTURE");
                return;  // 종료시간에서 10분을 초과하면 출석
            } else {
                this.kdtAttStatus = KDTAttStatus.valueOf("EARLY_LEAVE");
                return;
            }
        }

    }

    public void update(KDTAttDTO kdtAttDTO){
        // 입실 시간 업데이트
        if(kdtAttDTO.getKdtAttEntryTime() != null){
            this.kdtAttEntryTime = kdtAttDTO.getKdtAttEntryTime();
        }
        // 퇴실 시간 업데이트
        if(kdtAttDTO.getKdtAttExitTime() != null){
            this.kdtAttExitTime = kdtAttDTO.getKdtAttExitTime();
        }
        // 외출 시작 시간 업데이트
        if(kdtAttDTO.getKdtAttLeaveStart() != null){
            this.kdtAttLeaveStart = kdtAttDTO.getKdtAttLeaveStart();
        }
        // 외출 종료 시간 업데이트
        if(kdtAttDTO.getKdtAttLeaveEnd() != null){
            this.kdtAttLeaveEnd = kdtAttDTO.getKdtAttLeaveEnd();
        }
        // 상태 업데이트
        if(kdtAttDTO.getKdtAttStatus() != null){
            this.kdtAttStatus = KDTAttStatus.valueOf(kdtAttDTO.getKdtAttStatus());
        }
    }
}
