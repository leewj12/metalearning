package com.Meta_learning.KDT.KDTDTO.KDTSessionDTO;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTSessionViewDTO {

    private Long kdtSessionId;
    private Long kdtCourseId;  // 수정된 필드명 (Long 타입)
    private int kdtSessionNum;
    private String kdtSessionTitle;
    private String kdtSessionDescript;
    private LocalDate kdtSessionStartDate;
    private LocalDate kdtSessionEndDate;
    private String kdtSessionCategory;
    private int kdtSessionMaxCapacity;
    private String kdtSessionThumbnail;
    private LocalTime kdtSessionStartTime;
    private LocalTime kdtSessionEndTime;
    private String kdtSessionPostcode;
    private String kdtSessionAddress;
    private String kdtSessionAddressDetail;
    private Boolean kdtSessionOnline;
    private int kdtSessionTotalDay;       // 국비 총 교육일
    private int kdtSessionOnedayTime;     // 국비 교육 시간
    private int kdtSessionTotalTime;      // 국비 총 교육 시간
    private String kdtSessionStatus;      // 상태를 문자열로 처리
    private String kdtCourseType;         // 추가된 부분
    private String kdtSessionDday;        // D-Day 추가 필드

    // D-Day 계산 메서드 추가
    public String calculateDDay() {
        // 시작일시 만들기
        LocalDateTime startDateTime = LocalDateTime.of(kdtSessionStartDate, kdtSessionStartTime);

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // D-Day 계산
        long daysBetween = Duration.between(now, startDateTime).toDays();

        if (daysBetween > 0) {
            return "D-" + daysBetween;  // 시작일까지 남은 일수
        } else if (daysBetween == 0) {
            return "D-Day";  // 오늘 시작하는 경우
        } else {
            return "종료됨";  // 이미 종료된 경우
        }
    }

    // D-Day 값을 설정하는 메서드 추가
    public void setKdtSessionDday(String dday) {
        this.kdtSessionDday = dday;
    }

    // 엔티티를 DTO로 변환하는 메서드
    public static KDTSessionViewDTO convertSessionEntityToViewDTO(KDTSessionEntity session) {
        KDTSessionViewDTO dto = new KDTSessionViewDTO();
        dto.setKdtSessionId(session.getKdtSessionId());
        dto.setKdtCourseId(session.getKdtCourseEntity().getKdtCourseId());  // 과정 ID
        dto.setKdtSessionNum(session.getKdtSessionNum());
        dto.setKdtSessionTitle(session.getKdtSessionTitle());
        dto.setKdtSessionDescript(session.getKdtSessionDescript());
        dto.setKdtSessionStartDate(session.getKdtSessionStartDate());
        dto.setKdtSessionEndDate(session.getKdtSessionEndDate());
        dto.setKdtSessionCategory(session.getKdtSessionCategory());
        dto.setKdtSessionMaxCapacity(session.getKdtSessionMaxCapacity());
        dto.setKdtSessionThumbnail(session.getKdtSessionThumbnail());
        dto.setKdtSessionStartTime(session.getKdtSessionStartTime());
        dto.setKdtSessionEndTime(session.getKdtSessionEndTime());
        dto.setKdtSessionPostcode(session.getKdtSessionPostcode());
        dto.setKdtSessionAddress(session.getKdtSessionAddress());
        dto.setKdtSessionAddressDetail(session.getKdtSessionAddressDetail());
        dto.setKdtSessionOnline(session.getKdtSessionOnline());
        dto.setKdtSessionTotalDay(session.getKdtSessionTotalDay());
        dto.setKdtSessionOnedayTime(session.getKdtSessionOnedayTime());
        dto.setKdtSessionTotalTime(session.getKdtSessionTotalTime());
        dto.setKdtSessionStatus(session.getKdtSessionStatus().name()); // Enum -> String 변환

        // 과정 타입 추가
        if (session.getKdtCourseEntity() != null) {
            dto.setKdtCourseType(session.getKdtCourseEntity().getKdtCourseType());
        }

        // D-Day 계산하여 설정
        String dday = dto.calculateDDay();
        dto.setKdtSessionDday(dday);

        return dto;
    }
}
