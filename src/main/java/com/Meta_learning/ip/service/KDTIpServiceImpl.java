package com.Meta_learning.ip.service;

import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.ip.dto.requestDTO.KDTIpCreateDTO;
import com.Meta_learning.ip.dto.responseDTO.KDTIpViewDTO;
import com.Meta_learning.ip.entity.KDTIpEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.ip.repository.KDTIpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j  // 로그를 찍기 위한 어노테이션 추가
public class KDTIpServiceImpl implements KDTIpService {

    private final KDTIpRepository kdtIpRepository;  // KDTIpRepository 주입
    private final KDTSessionRepository kdtSessionRepository;  // KDTSessionRepository 주입

    // ip주소 등록하는 메서드임
    @Override
    public boolean ipsave(KDTIpCreateDTO ipCreateDTO) {
        try {
            // sessionId로 KDTSessionEntity 조회
            KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findById(ipCreateDTO.getKdtSessionId())
                    .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));  // 세션이 없으면 예외 발생

            // DTO -> Entity로 변환
            KDTIpEntity kdtIpEntity = ipCreateDTO.toEntity(kdtSessionEntity);  // DTO에서 Entity 변환

            // IP 저장
            kdtIpRepository.save(kdtIpEntity);  // KDTIpRepository를 통해 DB에 저장
            return true;  // 저장 성공 시 true 반환
        } catch (RuntimeException e) {
            // 예외 발생 시 예외 메시지 로그로 출력
            log.error("세션 조회 또는 IP 등록 중 오류 발생: {}", e.getMessage());
            return false;  // 예외 발생 시 false 반환
        } catch (Exception e) {
            // 다른 예외가 발생한 경우
            log.error("예기치 못한 오류 발생: {}", e.getMessage());
            return false;  // 예외 발생 시 false 반환
        }
    }

    @Override
    public List<KDTIpViewDTO> ipviewall(Long sessionId) {
        // 세션 ID로 KDTIpEntity 목록을 조회
        List<KDTIpEntity> kdtIpEntities = kdtIpRepository.findByKdtSessionEntity_KdtSessionId(sessionId);

        // KDTIpEntity 목록을 KDTIpViewDTO 목록으로 변환
        List<KDTIpViewDTO> kdtIpViewDTOS = kdtIpEntities.stream()
                .map(KDTIpViewDTO::toDTO)  // Entity -> DTO 변환
                .collect(Collectors.toList());  // List로 변환하여 변수에 저장

        // 변환된 리스트를 반환
        return kdtIpViewDTOS;
    }

    @Override
    public boolean deleteId(Long ipId) {
        try {
            // 해당 IP가 존재하는지 먼저 확인
            if (!kdtIpRepository.existsById(ipId)) {
                return false;  // 해당 IP가 존재하지 않으면 삭제 실패
            }

            // IP 삭제
            kdtIpRepository.deleteById(ipId);  // KDTIpRepository를 통해 IP 삭제
            return true;  // 삭제 성공
        } catch (Exception e) {
            return false;  // 예외 발생 시 삭제 실패
        }
    }

}
