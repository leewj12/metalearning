package com.Meta_learning.KDT.KDTservice.KDTTimeService;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KDTTimeService {

    private final KDTSessionRepository kdtSessionRepository;

    /**
     * 매일 자정(00:00:00)에 실행되는 메서드.
     * 종료일이 지난 세션 상태를 'FINISHED'로 업데이트합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정에 실행
    @Transactional
    public void updateSessionStatuses() {
        LocalDate currentDate = LocalDate.now();  // 현재 날짜를 가져옵니다.

        // 종료일이 지난 세션 상태를 'FINISHED'로 업데이트
        int updatedCount = kdtSessionRepository.updateSessionsToFinished(currentDate, KDTSessionStatus.FINISHED);

        // 업데이트된 세션의 수를 출력합니다.
        System.out.println("Updated sessions to FINISHED: " + updatedCount);
    }

    /**
     * 매일 아침 6시마다 실행되는 메서드.
     * 시작일이 오늘인 세션 상태를 'ONGOING'으로 업데이트합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")  // 매일 아침 6시에 실행 -  초 분 시 일 월 요일 연도
//@Scheduled(cron = "0 25 11 * * *")  // 매일 오전 11시 25분에 실행
    public void updateSessionsToOngoing() {
        LocalDate currentDate = LocalDate.now();  // 현재 날짜를 가져옵니다.

        // 시작일이 오늘인 세션 상태를 'ONGOING'으로 업데이트
        int updatedCount = kdtSessionRepository.updateSessionsToOngoing(currentDate, KDTSessionStatus.ONGOING);

        // 업데이트된 세션의 수를 출력합니다.
        System.out.println("Updated sessions to ONGOING: " + updatedCount);
    }

    /**
     * 종료 시간이 지나면 상태를 'FINISHED'로 업데이트
     * 시작일이 되면 상태를 'ONGOING'으로 업데이트하는 메서드.
     */
    public void updateSessionStatusOnScheduler(KDTSessionEntity session) {
        LocalDate currentDate = LocalDate.now();

        // 종료 시간이 지나면 'FINISHED'로 상태를 업데이트
        if (session.getKdtSessionEndDate().isBefore(currentDate) && session.getKdtSessionStatus() != KDTSessionStatus.FINISHED) {
            session.updateStatus(KDTSessionStatus.FINISHED);  // 상태를 'FINISHED'로 변경
        }

        // 시작일이 되면 'ONGOING'으로 상태를 업데이트
        if (session.getKdtSessionStartDate().isEqual(currentDate) && session.getKdtSessionStatus() != KDTSessionStatus.ONGOING) {
            session.updateStatus(KDTSessionStatus.ONGOING);  // 상태를 'ONGOING'으로 변경
        }
    }
}
