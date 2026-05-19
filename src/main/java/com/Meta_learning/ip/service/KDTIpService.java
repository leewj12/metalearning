package com.Meta_learning.ip.service;

import com.Meta_learning.ip.dto.requestDTO.KDTIpCreateDTO;
import com.Meta_learning.ip.dto.responseDTO.KDTIpViewDTO;

import java.util.List;

public interface KDTIpService {


    // ip주소 등록하는 메서드임
    boolean ipsave(KDTIpCreateDTO ipCreateDTO);


    //ip 목록 보는 거임
    List<KDTIpViewDTO> ipviewall(Long sessionId);

    // IP 삭제 메서드
    boolean deleteId(Long ipId);  // IP 삭제를 위한 메서드 선언



}
