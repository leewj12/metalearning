package com.Meta_learning.manager.managerrestcontroller;


import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTservice.detail.KdtDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class ManagerDetailRestController {
    private final KdtDetailService kdtDetailService;
    private final KDTSessionRepository kdtSessionRepository;

    @DeleteMapping("api/manager/KDT/{kdtSessionId}/detail/delete")
    public void deleteKdtCourseDetail(@PathVariable Long kdtSessionId){
        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTDetailEntity detail = kdtDetailService.getDetailBySessionId(kdtSessionId);

        kdtDetailService.deleteKdtDetail(detail.getKdtDetailId());
    }
}
