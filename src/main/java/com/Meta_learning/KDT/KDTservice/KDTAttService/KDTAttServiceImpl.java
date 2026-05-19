package com.Meta_learning.KDT.KDTservice.KDTAttService;


import com.Meta_learning.KDT.KDTDTO.KDTAttDTO.KDTAttDTO;
import com.Meta_learning.KDT.KDTDTO.KDTAttListDTO.KDTAttListDTO;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttEntity;
import com.Meta_learning.KDT.KDTentity.KDTAttEntity.KDTAttStatus;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTAttRepository.KDTAttRepository;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KDTAttServiceImpl implements KDTAttService {
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTPartRepository kdtPartRepository;
    private final KDTAttRepository kdtAttRepository;


    // 특정 회차의 모든 참석자(또는 특정 참석자)의 출석부 목록을 보여주는 메서드
    @Override
    public List<KDTAttListDTO> findKdtAttList(LocalDate date, Long sessionId, Long kdtPartId) {
        // 조건에 만족하는 엔티티 가져오기
        // partid== null이면 모든 참석자의 정보 가져오기
        List<KDTPartEntity> participants;
        if(kdtPartId == null){
            participants = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(sessionId);
        }else {
            participants = new ArrayList<>();
            participants.add(kdtPartRepository.findByKdtPartId(kdtPartId));
        }

        //if(participants == null || participants.isEmpty()) return null;
        List<KDTAttEntity> attentions = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(sessionId);
        //if(attentions == null || attentions.isEmpty()) return null;

        // 세션 정보 가져오기
        KDTSessionEntity sessionEntity = kdtSessionRepository.findByKdtSessionId(sessionId);
        int totalDay = sessionEntity.getKdtSessionTotalDay();           // 총 교육 일수(출석률을 계산하기 위해)
        LocalTime startTime = sessionEntity.getKdtSessionStartTime();   // 지각 여부 판단하기 위해

        // 출석 상태 별로 카운트
        Map<Long, Map<KDTAttStatus, Long>> statusCounts = attentions.stream()
                .collect(Collectors.groupingBy(
                        // 참가자id로 그룹화.
                        attention -> attention.getKdtPartEntity().getKdtPartId(),
                        // status를 기준으로 그룹화하고, 이 상태에서 Collectors.counting()을 사용하여 상태의 갯수를 셈.
                        Collectors.groupingBy(KDTAttEntity::getKdtAttStatus, Collectors.counting())
                ));

        // 지각, 조퇴, 외출 카운트
        Map<Long, Map<String, Long>> specialCounts = attentions.stream()
                .collect(Collectors.groupingBy(
                        attention -> attention.getKdtPartEntity().getKdtPartId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    // 지각, 조퇴, 외출 카운트를 담을 맵
                                    Map<String, Long> counts = new HashMap<>();
                                    counts.put("tardy", 0L);
                                    counts.put("earlyLeave", 0L);
                                    counts.put("outgoing", 0L);
                                    for (KDTAttEntity attention : list) {
                                        if(!attention.getKdtAttStatus().equals(KDTAttStatus.VACATION) &&
                                                !attention.getKdtAttStatus().equals(KDTAttStatus.SICK_LEAVE) &&
                                                !attention.getKdtAttStatus().equals(KDTAttStatus.ABSENT) &&
                                                !attention.getKdtAttStatus().equals(KDTAttStatus.EARLY_LEAVE)){

                                            // 지각 체크: 입실 시간이 세션 시작 시간보다 10분 늦었을 때
                                            if (attention.getKdtAttEntryTime() != null && startTime != null ){
                                                LocalTime entryTime = attention.getKdtAttEntryTime().toLocalTime(); // LocalTime으로 변환
                                                if (entryTime.isAfter(startTime.plusMinutes(10))) {
                                                    counts.put("tardy", counts.get("tardy") + 1);
                                                }
                                            }

                                            // 외출 체크: 외출 시작 시간이 존재할 때
                                            if (attention.getKdtAttLeaveStart() != null) {
                                                counts.put("outgoing", counts.get("outgoing") + 1);
                                            }
                                        }
                                    }
                                    return counts;
                                }
                        )
                ));

        // 각 학생별 DTO로 변환
        List<KDTAttListDTO> attentionList = participants.stream()
                .map(participant -> {
                    Long partId = participant.getKdtPartId();

                    // statusCount가 null일 경우 빈 맵으로 처리
                    Map<KDTAttStatus, Long> statusCount = statusCounts != null && statusCounts.containsKey(partId)
                            ? statusCounts.get(partId)
                            : new HashMap<>();

                    // specialCount가 null일 경우 빈 맵으로 처리
                    Map<String, Long> specialCount = specialCounts != null && specialCounts.containsKey(partId)
                            ? specialCounts.get(partId)
                            : new HashMap<>();
                    int departureCount = statusCount.getOrDefault(KDTAttStatus.DEPARTURE, 0L).intValue();       // 출석횟수
                    int vacationCount = statusCount.getOrDefault(KDTAttStatus.VACATION, 0L).intValue();         // 휴가
                    int sickLeaveCount = statusCount.getOrDefault(KDTAttStatus.SICK_LEAVE, 0L).intValue();      // 병결
                    int earlyLeaveCount = statusCount.getOrDefault(KDTAttStatus.EARLY_LEAVE, 0L).intValue();    // 조퇴
                    int absenceCount = statusCount.getOrDefault(KDTAttStatus.ABSENT, 0L).intValue();            // 결석
                    int tardyCount = specialCount.getOrDefault("tardy", 0L).intValue();                     // 지각횟수
                    int outgoingCount = specialCount.getOrDefault("outgoing", 0L).intValue();               // 외출횟수

                    absenceCount += (tardyCount / 3);
                    absenceCount += (outgoingCount / 3);

                    // 출석 횟수 계산
                    // getOrDefault(KDTAttStatus.ARRIVAL, 0L) : 없으면 0L을 기본값으로 반환.
                    int attendanceCount = departureCount
                            + vacationCount
                            + sickLeaveCount
                            + earlyLeaveCount
                            - (tardyCount / 3) - (outgoingCount / 3);
                    // 지각 3회당 출석 1회 차감, 조퇴 1회당 출석 1회 차감, 외출 3회당 출석 1회 차감

                    // 출석률 계산
                    double attendanceRate = totalDay > 0 ? (attendanceCount * 100.0) / totalDay : 0.0;

                    // 매개변수로 받은 날짜로 필터링
                    Optional<KDTAttEntity> attendanceOnGivenDate = attentions.stream()
                            .filter(att -> att.getKdtAttDate().equals(date)
                                    && att.getKdtPartEntity().getKdtPartId().equals(partId))
                            .findFirst(); // 첫번째 것만 가져옴(어차피 하루에 하나 일것)

                    // 반환
                    KDTAttListDTO kdtAttListDTO = new KDTAttListDTO();
                    kdtAttListDTO.setKdtPartId(partId);
                    kdtAttListDTO.setUserId(participant.getUserEntity().getUserId());
                    kdtAttListDTO.setKdtPartName(participant.getUserEntity().getName());
//                    kdtAttListDTO.setKdtAttStatus();
                    kdtAttListDTO.setKdtAttRate(attendanceRate);
                    kdtAttListDTO.setAttCount(attendanceCount);
                    kdtAttListDTO.setTardyCount(tardyCount);
                    kdtAttListDTO.setEarlyLeaveCount(earlyLeaveCount);
                    kdtAttListDTO.setOutgoingCount(outgoingCount);
                    kdtAttListDTO.setAbsenceCount(absenceCount);
//                    kdtAttListDTO.setKdtAttEntryTime(attention.getKdtAttEntryTime());
//                    kdtAttListDTO.setKdtAttExitTime(attention.getKdtAttExitTime());
                    // 필터링한 날에 해당하는 정보만 설정
                    attendanceOnGivenDate.ifPresent(att -> {
                        kdtAttListDTO.setKdtAttStatus(att.getKdtAttStatus().name()); // 오늘 날짜 출석 상태
                        kdtAttListDTO.setKdtAttEntryTime(att.getKdtAttEntryTime()); // 오늘 날짜 입실 시간
                        kdtAttListDTO.setKdtAttExitTime(att.getKdtAttExitTime()); // 오늘 날짜 퇴실 시간
                    });
                    return kdtAttListDTO;
                })
                .collect(Collectors.toList());
        return attentionList;
    }

    // 특정 참석자의 모든 출석 정보 가져오기
    @Override
    public List<KDTAttDTO> findKdtAtt(Long kdtPartId) {
        List<KDTAttEntity> kdtAttEntityList = kdtAttRepository.findByKdtPartEntity_KdtPartId(kdtPartId);
        return kdtAttEntityList.stream()
                .map(kdtAttEntity -> new KDTAttDTO(
                        kdtAttEntity.getKdtAttId(),
                        kdtAttEntity.getKdtPartEntity().getKdtPartId(), // KDTPartEntity에서 KDT_part_id를 가져옴
                        kdtAttEntity.getKdtAttDate(),
                        kdtAttEntity.getKdtAttEntryTime(),
                        kdtAttEntity.getKdtAttExitTime(),
                        kdtAttEntity.getKdtAttLeaveStart(),
                        kdtAttEntity.getKdtAttLeaveEnd(),
                        kdtAttEntity.getKdtAttStatus().getText()
                        //kdtAttEntity.getKdtPartEntity().getUserEntity().getUserId()
                ))
                .collect(Collectors.toList());  // 결과를 List로 수집
    }

    // 이미 있는 출석부인지 확인하는 메서드
    @Override
    public Boolean findKdtAtt(Long kdtPartId, LocalDate kdtAttDate) {
        if (kdtAttRepository.findByKdtPartEntity_KdtPartIdAndKdtAttDate(kdtPartId, kdtAttDate) == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public KDTAttEntity findKdtAttById(Long kdtAttID) {
        return kdtAttRepository.findByKdtAttId(kdtAttID);
    }

    // 새로운 출석부를 등록하는 메서드
    @Override
    public KDTAttEntity kdtAttSave(Long kdtSessionId, KDTAttDTO kdtAttDTO) {
        try{
            KDTPartEntity kdtPartEntity = kdtPartRepository.findByKdtPartId(kdtAttDTO.getKdtPartId());
            KDTAttEntity kdtAttEntity =KDTAttEntity.builder()
                    .kdtAttId(kdtAttDTO.getKdtAttId())
                    .kdtPartEntity(kdtPartEntity)  // 외래키 관계 설정
                    .kdtAttDate(kdtAttDTO.getKdtAttDate())
                    .kdtAttEntryTime(kdtAttDTO.getKdtAttEntryTime())
                    .kdtAttExitTime(kdtAttDTO.getKdtAttExitTime())
                    .kdtAttLeaveStart(kdtAttDTO.getKdtAttLeaveStart())
                    .kdtAttLeaveEnd(kdtAttDTO.getKdtAttLeaveEnd())
                    .kdtAttStatus(kdtAttDTO.getKdtAttStatus() != null
                            ? KDTAttStatus.valueOf(kdtAttDTO.getKdtAttStatus())
                            : KDTAttStatus.valueOf("ERROR"))
                    .build();
            KDTSessionEntity sessionEntity = kdtSessionRepository.findByKdtSessionId(kdtSessionId);
            LocalTime startTime = sessionEntity.getKdtSessionStartTime();
            LocalTime endTime = sessionEntity.getKdtSessionEndTime();
            kdtAttEntity.setStatus(startTime, endTime);

            return kdtAttRepository.save(kdtAttEntity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public KDTAttEntity updateKdtAtt(Long kdtAttId, Long kdtSessionId, KDTAttDTO kdtAttDTO) {
        try {
            KDTAttEntity kdtAttEntity = kdtAttRepository.findByKdtAttId(kdtAttId);
            kdtAttEntity.update(kdtAttDTO);

            KDTSessionEntity sessionEntity = kdtSessionRepository.findByKdtSessionId(kdtSessionId);
            LocalTime startTime = sessionEntity.getKdtSessionStartTime();
            LocalTime endTime = sessionEntity.getKdtSessionEndTime();
            kdtAttEntity.setStatus(startTime, endTime);

            kdtAttRepository.save(kdtAttEntity);
            return kdtAttRepository.findByKdtAttId(kdtAttId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteAtt(Long kdtAttId) {
        kdtAttRepository.deleteById(kdtAttId);
    }

    @Override
    public List<Map<String, Object>> getAllMonthlyAttendanceStats(Long kdtSessionId) {
        List<Map<String, Object>> monthlyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<YearMonth, Map<String, Integer>> tempStats = new HashMap<>();
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            YearMonth yearMonth = YearMonth.from(attEntity.getKdtAttDate()); // 출석 날짜에서 월 추출
            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 해당 월에 대한 통계 가져오기
            Map<String, Integer> statusCount = tempStats.getOrDefault(yearMonth, new HashMap<>());
            statusCount.put(status.name(), statusCount.getOrDefault(status.name(), 0) + 1);

            if (!attEntity.getKdtAttStatus().equals(KDTAttStatus.VACATION) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.SICK_LEAVE) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.ABSENT) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.EARLY_LEAVE)){
                // 휴가, 병결,
                if((attEntity.getKdtAttEntryTime() != null)) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCount.put("tardy", statusCount.getOrDefault("tardy", 0) + 1);
                    }

                    // 외출 체크: 외출 시작 시간이 존재할 때
                    if (attEntity.getKdtAttLeaveStart() != null) {
                        statusCount.put("outgoing", statusCount.getOrDefault("outgoing", 0) + 1);
                    }
                }
            }
            tempStats.put(yearMonth, statusCount);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<YearMonth, Map<String, Integer>> entry : tempStats.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            Map<String, Integer> statusCount = entry.getValue();

            Map<String, Object> yearMonthData = new HashMap<>();
            yearMonthData.put("year", yearMonth.getYear());
            yearMonthData.put("month", yearMonth.getMonthValue());
            yearMonthData.putAll(statusCount);  // 상태별 카운트 추가

            monthlyStats.add(yearMonthData);
        }
        return monthlyStats;
    }


    @Override
    public List<Map<String, Object>> getAllMonthlyAttendanceStatsWithAgeGroups(Long kdtSessionId) {
        List<Map<String, Object>> monthlyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<YearMonth, Map<String, Map<String, Integer>>> tempStats = new HashMap<>();  // 나이대별 통계 추가
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            YearMonth yearMonth = YearMonth.from(attEntity.getKdtAttDate()); // 출석 날짜에서 월 추출
            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 나이대 그룹핑
            UserEntity userEntity = attEntity.getKdtPartEntity().getUserEntity();
            String ageGroup = getAgeGroup(userEntity.getUserBirth());  // 나이대 그룹 계산

            // 해당 월과 나이대에 대한 통계 가져오기
            Map<String, Map<String, Integer>> statusCountForMonth = tempStats.getOrDefault(yearMonth, new HashMap<>());
            Map<String, Integer> statusCountForAgeGroup = statusCountForMonth.getOrDefault(ageGroup, new HashMap<>());

            statusCountForAgeGroup.put(status.name(), statusCountForAgeGroup.getOrDefault(status.name(), 0) + 1);
            if (!attEntity.getKdtAttStatus().equals(KDTAttStatus.VACATION) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.SICK_LEAVE) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.ABSENT) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.EARLY_LEAVE)){
                // 휴가, 병결,
                if((attEntity.getKdtAttEntryTime() != null)) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCountForAgeGroup.put("tardy", statusCountForAgeGroup.getOrDefault("tardy", 0) + 1);
                    }
                    // 외출 체크: 외출 시작 시간이 존재할 때
                    if (attEntity.getKdtAttLeaveStart() != null) {
                        statusCountForAgeGroup.put("outgoing", statusCountForAgeGroup.getOrDefault("outgoing", 0) + 1);
                    }
                }
            }

            statusCountForMonth.put(ageGroup, statusCountForAgeGroup);
            tempStats.put(yearMonth, statusCountForMonth);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<YearMonth, Map<String, Map<String, Integer>>> entry : tempStats.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            Map<String, Map<String, Integer>> statusCountForMonth = entry.getValue();

            Map<String, Object> yearMonthData = new HashMap<>();
            yearMonthData.put("year", yearMonth.getYear());
            yearMonthData.put("month", yearMonth.getMonthValue());

            // 나이대별 통계 추가
            for (Map.Entry<String, Map<String, Integer>> ageGroupEntry : statusCountForMonth.entrySet()) {
                String ageGroup = ageGroupEntry.getKey();
                Map<String, Integer> statusCount = ageGroupEntry.getValue();

                // 나이대별 출석 상태 통계 추가
                yearMonthData.put(ageGroup, statusCount);
            }

            monthlyStats.add(yearMonthData);
        }
        return monthlyStats;
    }

    @Override
    public List<Map<String, Object>> getAllMonthlyAttendanceStatsWithGenderGroups(Long kdtSessionId) {
        List<Map<String, Object>> monthlyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<YearMonth, Map<String, Map<String, Integer>>> tempStats = new HashMap<>();  // 성별별 통계 추가
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            YearMonth yearMonth = YearMonth.from(attEntity.getKdtAttDate()); // 출석 날짜에서 월 추출
            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 성별 그룹핑
            UserEntity userEntity = attEntity.getKdtPartEntity().getUserEntity();
            String gender = userEntity.getUserGender();  // 성별 그룹 계산

            // 해당 월과 성별에 대한 통계 가져오기
            Map<String, Map<String, Integer>> statusCountForMonth = tempStats.getOrDefault(yearMonth, new HashMap<>());
            Map<String, Integer> statusCountForGender = statusCountForMonth.getOrDefault(gender, new HashMap<>());

            statusCountForGender.put(status.name(), statusCountForGender.getOrDefault(status.name(), 0) + 1);

            if (!attEntity.getKdtAttStatus().equals(KDTAttStatus.VACATION) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.SICK_LEAVE) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.ABSENT) &&
                    !attEntity.getKdtAttStatus().equals(KDTAttStatus.EARLY_LEAVE)){
                // 휴가, 병결, 결석, 조퇴 제외
                if (attEntity.getKdtAttEntryTime() != null) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCountForGender.put("tardy", statusCountForGender.getOrDefault("tardy", 0) + 1);
                    }
                    // 외출 체크: 외출 시작 시간이 존재할 때
                    if (attEntity.getKdtAttLeaveStart() != null) {
                        statusCountForGender.put("outgoing", statusCountForGender.getOrDefault("outgoing", 0) + 1);
                    }
                }
            }

            statusCountForMonth.put(gender, statusCountForGender);
            tempStats.put(yearMonth, statusCountForMonth);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<YearMonth, Map<String, Map<String, Integer>>> entry : tempStats.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            Map<String, Map<String, Integer>> statusCountForMonth = entry.getValue();

            Map<String, Object> yearMonthData = new HashMap<>();
            yearMonthData.put("year", yearMonth.getYear());
            yearMonthData.put("month", yearMonth.getMonthValue());

            // 성별별 통계 추가
            for (Map.Entry<String, Map<String, Integer>> genderEntry : statusCountForMonth.entrySet()) {
                String gender = genderEntry.getKey();
                Map<String, Integer> statusCount = genderEntry.getValue();

                // 성별별 출석 상태 통계 추가
                yearMonthData.put(gender, statusCount);
            }

            monthlyStats.add(yearMonthData);
        }

        return monthlyStats;
    }

    @Override
    public List<Map<String, Object>> getAllWeeklyAttendanceStats(Long kdtSessionId) {
        List<Map<String, Object>> weeklyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다.
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<Integer, Map<String, Integer>> tempStats = new HashMap<>();
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            // 해당 출석 날짜에서 요일 추출 (0: 일요일, 6: 토요일)
            int dayOfWeek = attEntity.getKdtAttDate().getDayOfWeek().getValue() % 7; // 0 = 일요일, 6 = 토요일

            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 해당 요일에 대한 통계 가져오기
            Map<String, Integer> statusCount = tempStats.getOrDefault(dayOfWeek, new HashMap<>());

            // 상태별 카운트
            statusCount.put(status.name(), statusCount.getOrDefault(status.name(), 0) + 1);

            // 지각 체크 (입실 시간 체크)
            if (!status.equals(KDTAttStatus.VACATION) &&
                    !status.equals(KDTAttStatus.SICK_LEAVE) &&
                    !status.equals(KDTAttStatus.ABSENT) &&
                    !status.equals(KDTAttStatus.EARLY_LEAVE)) {
                if (attEntity.getKdtAttEntryTime() != null) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCount.put("tardy", statusCount.getOrDefault("tardy", 0) + 1); // 지각 카운트
                    }
                }
            }

            // 외출 체크 (외출 시작 시간이 존재할 때)
            if (attEntity.getKdtAttLeaveStart() != null) {
                statusCount.put("outgoing", statusCount.getOrDefault("outgoing", 0) + 1); // 외출 카운트
            }

            // 요일별 통계 업데이트
            tempStats.put(dayOfWeek, statusCount);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<Integer, Map<String, Integer>> entry : tempStats.entrySet()) {
            int dayOfWeek = entry.getKey();
            Map<String, Integer> statusCount = entry.getValue();

            // 요일 이름 (0: 일요일, 6: 토요일)
            String dayName = getDayName(dayOfWeek);

            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("day", dayName);
            dayStats.putAll(statusCount);  // 상태별 카운트 추가

            weeklyStats.add(dayStats);
        }

        return weeklyStats;
    }

    @Override
    public List<Map<String, Object>> getAllWeeklyAttendanceStatsWithAgeGroups(Long kdtSessionId) {
        List<Map<String, Object>> weeklyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다.
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<Integer, Map<String, Map<String, Integer>>> tempStats = new HashMap<>();  // 나이대별 통계 추가
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            // 해당 출석 날짜에서 요일 추출 (0: 일요일, 6: 토요일)
            int dayOfWeek = attEntity.getKdtAttDate().getDayOfWeek().getValue() % 7; // 0 = 일요일, 6 = 토요일
            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 나이대 그룹핑
            UserEntity userEntity = attEntity.getKdtPartEntity().getUserEntity();
            String ageGroup = getAgeGroup(userEntity.getUserBirth());  // 나이대 그룹 계산

            // 해당 요일과 나이대에 대한 통계 가져오기
            Map<String, Map<String, Integer>> statusCountForDayOfWeek = tempStats.getOrDefault(dayOfWeek, new HashMap<>());
            Map<String, Integer> statusCountForAgeGroup = statusCountForDayOfWeek.getOrDefault(ageGroup, new HashMap<>());

            // 상태별 카운트
            statusCountForAgeGroup.put(status.name(), statusCountForAgeGroup.getOrDefault(status.name(), 0) + 1);

            // 지각 체크 (입실 시간 체크)
            if (!status.equals(KDTAttStatus.VACATION) &&
                    !status.equals(KDTAttStatus.SICK_LEAVE) &&
                    !status.equals(KDTAttStatus.ABSENT) &&
                    !status.equals(KDTAttStatus.EARLY_LEAVE)) {
                if (attEntity.getKdtAttEntryTime() != null) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCountForAgeGroup.put("tardy", statusCountForAgeGroup.getOrDefault("tardy", 0) + 1); // 지각 카운트
                    }
                }
            }

            // 외출 체크 (외출 시작 시간이 존재할 때)
            if (attEntity.getKdtAttLeaveStart() != null) {
                statusCountForAgeGroup.put("outgoing", statusCountForAgeGroup.getOrDefault("outgoing", 0) + 1); // 외출 카운트
            }

            // 요일별, 나이대별 통계 업데이트
            statusCountForDayOfWeek.put(ageGroup, statusCountForAgeGroup);
            tempStats.put(dayOfWeek, statusCountForDayOfWeek);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<Integer, Map<String, Map<String, Integer>>> entry : tempStats.entrySet()) {
            int dayOfWeek = entry.getKey();
            Map<String, Map<String, Integer>> statusCountForDayOfWeek = entry.getValue();

            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("day", getDayName(dayOfWeek));  // 요일 이름

            // 나이대별 통계 추가
            for (Map.Entry<String, Map<String, Integer>> ageGroupEntry : statusCountForDayOfWeek.entrySet()) {
                String ageGroup = ageGroupEntry.getKey();
                Map<String, Integer> statusCount = ageGroupEntry.getValue();

                // 나이대별 출석 상태 통계 추가
                dayStats.put(ageGroup, statusCount);
            }

            weeklyStats.add(dayStats);
        }

        return weeklyStats;
    }


    @Override
    public List<Map<String, Object>> getAllWeeklyAttendanceStatsWithGenderGroups(Long kdtSessionId) {
        List<Map<String, Object>> weeklyStats = new ArrayList<>();

        // 출석부에서 해당 KDTSessionId의 모든 출석 기록을 가져옵니다.
        List<KDTAttEntity> attList = kdtAttRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionId(kdtSessionId);

        // 출석 상태별 카운트 초기화
        Map<Integer, Map<String, Map<String, Integer>>> tempStats = new HashMap<>();  // 성별별 통계 추가
        LocalTime startTime = kdtSessionRepository.findByKdtSessionId(kdtSessionId).getKdtSessionStartTime();

        for (KDTAttEntity attEntity : attList) {
            // 해당 출석 날짜에서 요일 추출 (0: 일요일, 6: 토요일)
            int dayOfWeek = attEntity.getKdtAttDate().getDayOfWeek().getValue() % 7; // 0 = 일요일, 6 = 토요일
            KDTAttStatus status = attEntity.getKdtAttStatus();

            // 성별 그룹핑
            UserEntity userEntity = attEntity.getKdtPartEntity().getUserEntity();
            String genderGroup = userEntity.getUserGender().equals("M") ? "남성" : "여성";  // 성별 그룹 계산

            // 해당 요일과 성별에 대한 통계 가져오기
            Map<String, Map<String, Integer>> statusCountForDayOfWeek = tempStats.getOrDefault(dayOfWeek, new HashMap<>());
            Map<String, Integer> statusCountForGenderGroup = statusCountForDayOfWeek.getOrDefault(genderGroup, new HashMap<>());

            // 상태별 카운트
            statusCountForGenderGroup.put(status.name(), statusCountForGenderGroup.getOrDefault(status.name(), 0) + 1);

            // 지각 체크 (입실 시간 체크)
            if (!status.equals(KDTAttStatus.VACATION) &&
                    !status.equals(KDTAttStatus.SICK_LEAVE) &&
                    !status.equals(KDTAttStatus.ABSENT) &&
                    !status.equals(KDTAttStatus.EARLY_LEAVE)) {
                if (attEntity.getKdtAttEntryTime() != null) {
                    LocalTime entryTime = attEntity.getKdtAttEntryTime().toLocalTime();
                    if (entryTime.isAfter(startTime.plusMinutes(10))) {
                        statusCountForGenderGroup.put("tardy", statusCountForGenderGroup.getOrDefault("tardy", 0) + 1); // 지각 카운트
                    }
                }
            }

            // 외출 체크 (외출 시작 시간이 존재할 때)
            if (attEntity.getKdtAttLeaveStart() != null) {
                statusCountForGenderGroup.put("outgoing", statusCountForGenderGroup.getOrDefault("outgoing", 0) + 1); // 외출 카운트
            }

            // 요일별, 성별별 통계 업데이트
            statusCountForDayOfWeek.put(genderGroup, statusCountForGenderGroup);
            tempStats.put(dayOfWeek, statusCountForDayOfWeek);
        }

        // 결과를 원하는 형식으로 변환
        for (Map.Entry<Integer, Map<String, Map<String, Integer>>> entry : tempStats.entrySet()) {
            int dayOfWeek = entry.getKey();
            Map<String, Map<String, Integer>> statusCountForDayOfWeek = entry.getValue();

            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("day", getDayName(dayOfWeek));  // 요일 이름

            // 성별별 통계 추가
            for (Map.Entry<String, Map<String, Integer>> genderGroupEntry : statusCountForDayOfWeek.entrySet()) {
                String genderGroup = genderGroupEntry.getKey();
                Map<String, Integer> statusCount = genderGroupEntry.getValue();

                // 성별별 출석 상태 통계 추가
                dayStats.put(genderGroup, statusCount);
            }

            weeklyStats.add(dayStats);
        }

        return weeklyStats;
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0: return "일요일";
            case 1: return "월요일";
            case 2: return "화요일";
            case 3: return "수요일";
            case 4: return "목요일";
            case 5: return "금요일";
            case 6: return "토요일";
            default: return "알 수 없음";
        }
    }

    private String getAgeGroup(LocalDate birthDate) {
        // 나이 계산
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 나이대 그룹 반환
        if (age >= 16 && age <= 19) {
            return "10대 후반";
        } else if (age >= 20 && age <= 24) {
            return "20대 초반";
        } else if (age >= 25 && age <= 29) {
            return "20대 중반";
        } else if (age >= 30 && age <= 34) {
            return "20대 후반";
        } else if (age >= 35 && age <= 39) {
            return "30대 초반";
        } else if (age >= 40 && age <= 44) {
            return "30대 중반";
        } else {
            return "기타";
        }
    }
}
