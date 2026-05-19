package com.Meta_learning.KDT.KDTservice.KDTConsultService;


import com.Meta_learning.KDT.KDTDTO.KDTConsultDTO.KDTConsultDTO;
import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface KDTConsultService {
    // 세션에 등록된 학생의 이름과 참가 번호 리스트를 불러오는 메서드
    Map<Long, String> findRegisteredStudent(Long kdtSessionId);

    // 상담내역을 저장하는 메서드
    KDTConsultEntity kdtConsultSave(KDTConsultDTO kdtConsultDTO);

    // 상세 상담일지를 조회하는 메서드
    KDTConsultDTO findKdtConsultById(Long kdtConsultId);

    // 상담일지 삭제하는 메서드
    void deleteConsult(Long kdtConsultId);

    // 상담일지 검색 페이징 메서드
    Page<KDTConsultDTO> findAllWithPaging(Long kdtSessionId, Pageable pageable);

    // 삼담일지 페이징 메서드
    Page<KDTConsultDTO> searchWithPaging(Long kdtSessionId, String searchCategory, String search, Pageable pageable);
}
