package com.Meta_learning.KDT.KDTservice.KDTTrainService;


import com.Meta_learning.KDT.KDTDTO.KDTTrainDTO.KDTTrainDTO;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.KDT.KDTentity.KDTTrainEntity.KDTTrainEntity;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import com.Meta_learning.KDT.KDTrepository.KDTTrainRepository.KDTTrainRepository;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KDTTrainServiceImpl implements KDTTrainService {
    private final KDTStaffRepository kdtStaffRepository;
    private final KDTTrainRepository kdtTrainRepository;
    private final UserRepository userRepository;
    private final KDTSessionRepository kdtSessionRepository;

    // 세션에 참가한 강사의 Staff id와 강사의 이름을 반환하는 메서드
    @Override
    public Map<Long, String> findRegisteredInstr(Long sessionId) {
        List<KDTStaffEntity> kdtStaffEntities = kdtStaffRepository.findByKdtSessionEntity_KdtSessionIdAndUserEntity_userRole(sessionId, "INSTRUCTOR");
        // 결과를 Map 형태로 변환
        Map<Long, String> result = new HashMap<>();
        for (KDTStaffEntity kdtStaffEntity : kdtStaffEntities) {
            result.put(kdtStaffEntity.getKdtStaffId(), kdtStaffEntity.getUserEntity().getName());
        }
        return result;
    }

    // 훈련일지 존재 여부 확인
    @Override
    public Long findKdtTrain(Long kdtSessionId, LocalDate kdtTrainDate) {
        KDTTrainEntity kdtTrainEntity =kdtTrainRepository.findByKdtSessionEntity_KdtSessionIdAndKdtTrainDate(kdtSessionId, kdtTrainDate);
        if(kdtTrainEntity ==null) {
            return null;
        }
        return kdtTrainEntity.getKdtTrainId();
    }

    // 훈련일지 상세 조회
    @Override
    public KDTTrainDTO findKdtTrainById(Long kdtTrainId){
        KDTTrainEntity kdtTrainEntity = kdtTrainRepository.findByKdtTrainId(kdtTrainId);
        if(kdtTrainEntity == null) return null;

        KDTTrainDTO kdtTrainDTO = new KDTTrainDTO();

        // Set the values using setters
        kdtTrainDTO.setKdtTrainId(kdtTrainEntity.getKdtTrainId());
        kdtTrainDTO.setUserId(kdtTrainEntity.getUserEntity().getUserId());
        kdtTrainDTO.setKdtSessionId(kdtTrainEntity.getKdtSessionEntity().getKdtSessionId());
        kdtTrainDTO.setKdtStaffId(kdtTrainEntity.getKdtStaffEntity().getKdtStaffId());
        kdtTrainDTO.setKdtTrainTitle(kdtTrainEntity.getKdtTrainTitle());
        kdtTrainDTO.setKdtTrainDate(kdtTrainEntity.getKdtTrainDate());
        kdtTrainDTO.setKdtTrainContent(kdtTrainEntity.getKdtTrainContent());
        kdtTrainDTO.setKdtTrainSubject(kdtTrainEntity.getKdtTrainSubject());

        return kdtTrainDTO;
    }

    // 훈련일지 저장
    @Override
    public KDTTrainEntity kdtTrainSave(KDTTrainDTO kdtTrainDTO) {
        // dto를 entity로 변환
        KDTTrainEntity kdtTrainEntity =KDTTrainEntity.builder()
                .kdtTrainId(kdtTrainDTO.getKdtTrainId())                          // DTO에서 ID 값이 있다면 설정
                .userEntity(userRepository.findByUserId(kdtTrainDTO.getUserId()))                          // 외래 키인 UserEntity
                .kdtSessionEntity(kdtSessionRepository.findByKdtSessionId(kdtTrainDTO.getKdtSessionId()))              // 외래 키인 KDTSessionEntity
                .kdtStaffEntity(kdtStaffRepository.findByKdtStaffId(kdtTrainDTO.getKdtStaffId()))                  // 외래 키인 KDTStaffEntity
                .kdtTrainTitle(kdtTrainDTO.getKdtTrainTitle())                    // 제목
                .kdtTrainDate(kdtTrainDTO.getKdtTrainDate())                      // 훈련 날짜
                .kdtTrainContent(kdtTrainDTO.getKdtTrainContent())                // 내용
                .kdtTrainSubject(kdtTrainDTO.getKdtTrainSubject())                // 과목
                .build();
        return kdtTrainRepository.save(kdtTrainEntity);
    }

    @Override
    public void deleteTrain(Long kdtTrainId) {
        kdtTrainRepository.deleteById(kdtTrainId);
    }

    @Override
    public List<KDTTrainDTO> findKdtTrainListBySessionId(Long kdtSessionId) {
        List<KDTTrainEntity> kdtTrainEntities = kdtTrainRepository.findByKdtSessionEntity_KdtSessionIdOrderByKdtTrainDateDesc(kdtSessionId);
        return kdtTrainEntities.stream()
                .map(kdtTrainEntity -> new KDTTrainDTO(
                        kdtTrainEntity.getKdtTrainId(),
                        kdtTrainEntity.getUserEntity().getUserId(),  // UserEntity에서 UserId를 가져오기
                        kdtTrainEntity.getUserEntity().getName(),
                        kdtTrainEntity.getKdtSessionEntity().getKdtSessionId(),  // KDTSessionEntity에서 KdtSessionId 가져오기
                        kdtTrainEntity.getKdtStaffEntity().getKdtStaffId(),  // KDTStaffEntity에서 KDTStaffId 가져오기
                        kdtTrainEntity.getKdtTrainTitle(),
                        kdtTrainEntity.getKdtTrainDate(),
                        kdtTrainEntity.getKdtTrainContent(),
                        kdtTrainEntity.getKdtTrainSubject()
                ))
                .collect(Collectors.toList());
    }
}
