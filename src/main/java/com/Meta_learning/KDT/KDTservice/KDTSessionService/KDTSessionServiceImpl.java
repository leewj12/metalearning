package com.Meta_learning.KDT.KDTservice.KDTSessionService;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KDTSessionServiceImpl implements KDTSessionService {

    private final KDTSessionRepository kdtSessionRepository;

    // 세션 정보를 가져오는 메서드
    @Override
    public KDTSessionDTO findKdtSessionDTOById(Long sessionId) {
        KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findByKdtSessionId(sessionId);
        if (kdtSessionEntity==null) return null;

        KDTSessionDTO kdtSessionDTO = new KDTSessionDTO();
        kdtSessionDTO.setKdtSessionId(kdtSessionEntity.getKdtSessionId());
        kdtSessionDTO.setKdtCourseId(kdtSessionEntity.getKdtCourseEntity().getKdtCourseId()); // 연관된 Course ID도 DTO로 설정
        kdtSessionDTO.setKdtSessionNum(kdtSessionEntity.getKdtSessionNum());
        kdtSessionDTO.setKdtSessionTitle(kdtSessionEntity.getKdtSessionTitle());
        kdtSessionDTO.setKdtSessionDescript(kdtSessionEntity.getKdtSessionDescript());
        kdtSessionDTO.setKdtSessionStartDate(kdtSessionEntity.getKdtSessionStartDate());
        kdtSessionDTO.setKdtSessionEndDate(kdtSessionEntity.getKdtSessionEndDate());
        kdtSessionDTO.setKdtSessionCategory(kdtSessionEntity.getKdtSessionCategory());
        kdtSessionDTO.setKdtSessionMaxCapacity(kdtSessionEntity.getKdtSessionMaxCapacity());
        kdtSessionDTO.setKdtSessionThumbnail(kdtSessionEntity.getKdtSessionThumbnail());
        kdtSessionDTO.setKdtSessionStartTime(kdtSessionEntity.getKdtSessionStartTime());
        kdtSessionDTO.setKdtSessionEndTime(kdtSessionEntity.getKdtSessionEndTime());
        kdtSessionDTO.setKdtSessionPostcode(kdtSessionEntity.getKdtSessionPostcode());
        kdtSessionDTO.setKdtSessionAddress(kdtSessionEntity.getKdtSessionAddress());
        kdtSessionDTO.setKdtSessionAddressDetail(kdtSessionEntity.getKdtSessionAddressDetail());
        kdtSessionDTO.setKdtSessionOnline(kdtSessionEntity.getKdtSessionOnline());
        return kdtSessionDTO;
    }
}