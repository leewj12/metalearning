package com.Meta_learning.KDT.KDTservice.KDTPartservice;


import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartDTO;
import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartStatus;
import com.Meta_learning.user.userdto.UserPartDTO;

import java.util.List;

public interface KDTPartservice {

    // 회차에 학생 등록을 위한 메서드 선언
    int studentSessionsSave(KDTPartDTO kdtPartDTO);

    // 세션 ID로 총 학생 수를 구하는 메서드
    KDTPartTotalDTO studentCountAll(Long sessionId);

    //세션 ID와 userID로 PartId 찾는 메서드
    Long findPartIdBySessionIdAndUserId(Long kdtSessionId, Long userId);

    //세션 id로 유저 정보들 찾는 메서드 수강신청 등록된 학생 정보 찾는거임
    List<UserPartDTO>  userpartall(Long sessionId);

    //참여한 회차별 삭제하는 메서드
    boolean deleteUserPart(Long sessionId, Long kdtPartId);

    //참여한 회차 사람들 상태 수정
    boolean updateUserPart(Long sessionId, Long kdtPartId, KDTPartStatus newStatus, Boolean newEmploymentStatus);

}
