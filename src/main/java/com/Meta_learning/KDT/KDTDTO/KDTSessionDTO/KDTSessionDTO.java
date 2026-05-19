package com.Meta_learning.KDT.KDTDTO.KDTSessionDTO;


import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KDTSessionDTO {

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

    private String kdtSessionStatus; // 상태를 문자열로 처리
    private String kdtSessionDday;  // D-Day 정보를 추가

    // 엔티티를 DTO로 변환하는 메서드
    public static KDTSessionDTO convertSessionEntityToDTO(KDTSessionEntity sessionEntity) {
        return KDTSessionDTO.builder()
                .kdtSessionId(sessionEntity.getKdtSessionId())
                .kdtCourseId(sessionEntity.getKdtCourseEntity().getKdtCourseId())  // KDTSessionEntity에서 KdtCourseId 가져오기
                .kdtSessionNum(sessionEntity.getKdtSessionNum())
                .kdtSessionTitle(sessionEntity.getKdtSessionTitle())
                .kdtSessionDescript(sessionEntity.getKdtSessionDescript())
                .kdtSessionStartDate(sessionEntity.getKdtSessionStartDate())
                .kdtSessionEndDate(sessionEntity.getKdtSessionEndDate())
                .kdtSessionCategory(sessionEntity.getKdtSessionCategory())
                .kdtSessionMaxCapacity(sessionEntity.getKdtSessionMaxCapacity())
                .kdtSessionThumbnail(sessionEntity.getKdtSessionThumbnail())
                .kdtSessionStartTime(sessionEntity.getKdtSessionStartTime())
                .kdtSessionEndTime(sessionEntity.getKdtSessionEndTime())
                .kdtSessionPostcode(sessionEntity.getKdtSessionPostcode())
                .kdtSessionAddress(sessionEntity.getKdtSessionAddress())
                .kdtSessionAddressDetail(sessionEntity.getKdtSessionAddressDetail())
                .kdtSessionOnline(sessionEntity.getKdtSessionOnline())
                .kdtSessionTotalDay(sessionEntity.getKdtSessionTotalDay())
                .kdtSessionOnedayTime(sessionEntity.getKdtSessionOnedayTime())
                .kdtSessionTotalTime(sessionEntity.getKdtSessionTotalTime())
                .kdtSessionStatus(sessionEntity.getKdtSessionStatus().name())  // KDTSessionStatus Enum을 문자열로 변환
                .build();
    }
}

