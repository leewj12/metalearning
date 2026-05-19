package com.Meta_learning.KDT.KDTservice.KDTSessionService;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;

public interface KDTSessionService {
    // 세션 ID로 세션 정보 가져오기
    KDTSessionDTO findKdtSessionDTOById(Long sessionId);
}
