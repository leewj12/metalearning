package com.Meta_learning.manager.managerservice;

import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;

import java.util.List;

public interface ManagerService {

    //담당자의 정보로 국비과정 불러오는 메서드
    List<KDTCourseDTO> getCoursesByUser(Long userId);

    //담당자의 정보로 회차과정 불러오는 메서드
    List<KDTSessionDTO> getSessionsByUserId(Long userId);
}
