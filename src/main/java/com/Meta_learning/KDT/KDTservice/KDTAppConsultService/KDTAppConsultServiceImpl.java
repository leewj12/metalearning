package com.Meta_learning.KDT.KDTservice.KDTAppConsultService;


import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsult;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTAppConsultRepository.KDTAppConsultRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class KDTAppConsultServiceImpl implements KDTAppConsultService {
    private final KDTAppConsultRepository kdtAppConsultRepository;
    private final KDTSessionRepository kdtSessionRepository;

    // 비회원 수강신청한 사람  찾는 메서드
    @Override
    public KDTAppConsultDTO appConsultSave(Long sessionId, Long consultId) {
        // 세션 정보가 존재하는지 확인
        KDTSessionEntity session = kdtSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));

        // 세션 ID와 상담 ID를 기반으로 상담 정보 조회
        KDTAppConsult consult = kdtAppConsultRepository.findBySessionIdAndConsultId(sessionId, consultId)
                .orElseThrow(() -> new RuntimeException("해당 상담 정보를 찾을 수 없습니다."));

        // KDTAppConsultDTO로 변환 후 반환
        return KDTAppConsultDTO.convertEntityToDTO(consult);
    }

    //비회원 수강신청 상담일지 수정하는 메서드
    @Override
    public boolean appConsultFindById(Long sessionId, Long consultId, KDTAppConsultDTO kdtAppConsultDTO) {
        try {
            // consultId와 sessionId로 상담 정보 조회
            KDTAppConsult consult = kdtAppConsultRepository.findByKdtSessionEntity_KdtSessionIdAndKdtAppConsultId(sessionId, consultId)
                    .orElseThrow(() -> new RuntimeException("Consultation not found"));

            // 조회한 상담 정보를 DTO를 기준으로 업데이트
            consult.update(kdtAppConsultDTO);

            // 저장된 상담 정보 업데이트
            kdtAppConsultRepository.save(consult);

            return true;  // 수정 완료 후 true 반환
        } catch (Exception e) {
            return false;  // 오류 발생 시 false 반환
        }
    }


}
