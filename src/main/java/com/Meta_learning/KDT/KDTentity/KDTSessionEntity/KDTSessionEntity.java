package com.Meta_learning.KDT.KDTentity.KDTSessionEntity;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTCourseEntity.KDTCourseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "KDT_session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class KDTSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_session_id")
    private Long kdtSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_course_id", referencedColumnName = "KDT_course_id", nullable = false)
    private KDTCourseEntity kdtCourseEntity;

    @Column(name = "KDT_session_num", nullable = false)
    private int kdtSessionNum;  //회차 번호 시작은 1번

    @Column(name = "KDT_session_title", nullable = false)
    private String kdtSessionTitle;     // 회차 명

    @Column(name = "KDT_session_descript")
    private String kdtSessionDescript;  // 회차 간단 설명

    @Column(name = "KDT_session_start_date")
    private LocalDate kdtSessionStartDate;  // 회차 시작일

    @Column(name = "KDT_session_end_date")
    private LocalDate kdtSessionEndDate;    // 회차 종료일

    @Column(name = "KDT_session_category")
    private String kdtSessionCategory;      // 회차 카테고리

    @Column(name = "KDT_session_max_capacity")
    private int kdtSessionMaxCapacity;      // 회차 최대인원

    @Column(name = "KDT_session_thumbnail")
    private String kdtSessionThumbnail;     // 회차 썸네일

    @Column(name = "KDT_session_start_time")
    private LocalTime kdtSessionStartTime;  // 회차 시작시간

    @Column(name = "KDT_session_end_time")
    private LocalTime kdtSessionEndTime;    // 회차 종료시간

    @Column(name = "KDT_session_postcode")
    private String kdtSessionPostcode;      //회차 우편번호

    @Column(name = "KDT_session_address")
    private String kdtSessionAddress;       // 회차 주소

    @Column(name = "KDT_session_address_detail")
    private String kdtSessionAddressDetail; // 회차 상세 주소

    @Column(name = "KDT_session_online")
    private Boolean kdtSessionOnline;       // 온라인 여부

    @Column(name = "KDT_session_total_day")
    private int kdtSessionTotalDay;       // 국비 총 교육일

    @Column(name = "KDT_session_oneday_time")
    private int kdtSessionOnedayTime;       // 국비 교육 시간

    @Column(name = "KDT_session_total_time")
    private int kdtSessionTotalTime;       // 국비 총 교육 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "KDT_session_status", nullable = false)
    private KDTSessionStatus kdtSessionStatus; // 회차 상태


    // 스케줄러로 국비 종료시간이 자니면 자동으로 종료댐
    public void updateStatus(KDTSessionStatus newStatus) {
        this.kdtSessionStatus = newStatus;
    }



    //회차 수정하는 메서드임
    public void updateSessionDetails(KDTSessionDTO sessionDTO) {
        // DTO로 받은 데이터를 엔티티의 필드에 설정
        this.kdtSessionTitle = sessionDTO.getKdtSessionTitle();
        this.kdtSessionDescript = sessionDTO.getKdtSessionDescript();
        this.kdtSessionStartDate = sessionDTO.getKdtSessionStartDate();
        this.kdtSessionEndDate = sessionDTO.getKdtSessionEndDate();
        this.kdtSessionCategory = sessionDTO.getKdtSessionCategory();
        this.kdtSessionMaxCapacity = sessionDTO.getKdtSessionMaxCapacity();
        this.kdtSessionThumbnail = sessionDTO.getKdtSessionThumbnail();
        this.kdtSessionStartTime = sessionDTO.getKdtSessionStartTime();
        this.kdtSessionEndTime = sessionDTO.getKdtSessionEndTime();
        this.kdtSessionPostcode = sessionDTO.getKdtSessionPostcode();
        this.kdtSessionAddress = sessionDTO.getKdtSessionAddress();
        this.kdtSessionAddressDetail = sessionDTO.getKdtSessionAddressDetail();
        this.kdtSessionOnline = sessionDTO.getKdtSessionOnline();
        this.kdtSessionTotalDay = sessionDTO.getKdtSessionTotalDay();
        this.kdtSessionOnedayTime = sessionDTO.getKdtSessionOnedayTime();
        this.kdtSessionTotalTime = sessionDTO.getKdtSessionTotalTime();

        // Enum 값 수정 (name()을 사용하여 텍스트 기반으로 Enum을 찾음)
        this.kdtSessionStatus = KDTSessionStatus.valueOf(sessionDTO.getKdtSessionStatus());
    }


}
