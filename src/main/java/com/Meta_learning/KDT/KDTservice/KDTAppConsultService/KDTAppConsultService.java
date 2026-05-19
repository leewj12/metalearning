package com.Meta_learning.KDT.KDTservice.KDTAppConsultService;


import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;

public interface KDTAppConsultService {

    //비회원 상담 신청 저장하는 메서드
    KDTAppConsultDTO appConsultSave(Long sessionId, Long consultId);

    //비회원 상담 신청 수정하는 메서드
    boolean appConsultFindById(Long sessionId, Long consultId, KDTAppConsultDTO kdtAppConsultDTO);  // 수정된 메서드 시그니처
}
