package com.Meta_learning.student.studentpermissionservice;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionEvalDTO.KDTSessionEvalDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEvalEntity.KDTSessionEvalEntity;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionEvalRepository.KDTSessionEvalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final KDTPartRepository kdtPartRepository;
    private final KDTSessionEvalRepository kdtSessionEvalRepository;


    @Override
    public List<KDTSessionDTO> getSessionsByUserId(Long userId) {
        // 유저 ID로 해당 유저가 배정된 KDTPartEntity 정보를 가져옵니다.
        List<KDTPartEntity> partEntities = kdtPartRepository.findByUserEntity_UserId(userId);

        // 세션 정보가 없으면 빈 리스트 반환
        if (partEntities.isEmpty()) {
            return List.of();  // 빈 리스트 반환
        }

        // 각 세션에 대해 필요한 정보를 DTO로 변환하여 반환
        List<KDTSessionDTO> getSessions = partEntities.stream()
                .map(part -> part.getKdtSessionEntity())  // KDTPartEntity에서 KDTSessionEntity 가져오기
                .filter(Objects::nonNull)  // null 값 필터링 (null이 있을 수 있으므로 안전하게 처리)
                .distinct()  // 중복된 세션을 제거
                .map(KDTSessionDTO::convertSessionEntityToDTO)  // KDTSessionEntity -> KDTSessionDTO 변환
                .collect(Collectors.toList());

        // 세션 목록 반환
        return getSessions;
    }

    @Override
    public int saveCourseReview(KDTSessionEvalDTO kdtSessionEvalDTO, Long userId) {
        // userId로 여러 개의 KDTPartEntity를 조회
        List<KDTPartEntity> kdtPartEntities = kdtPartRepository.findByUserEntity_UserId(userId);

        // KDTPartEntity가 없으면 회차에 등록되지 않았다는 메시지 반환
        if (kdtPartEntities.isEmpty()) {
            return 1;  // 회차에 등록되지 않았습니다
        }

        // 첫 번째 KDTPartEntity를 가져옵니다 (여러 개일 수 있지만 여기서는 첫 번째만 사용)
        KDTPartEntity kdtPartEntity = kdtPartEntities.get(0);

        // 이미 리뷰를 작성했는지 확인
        Optional<KDTSessionEvalEntity> existingReview = kdtSessionEvalRepository
                .findByKdtPartEntity(kdtPartEntity);  // 기존 리뷰가 있는지 조회

        if (existingReview.isPresent()) {
            return 2;  // 이미 리뷰가 등록되었습니다
        }

        // DTO에 KDTPartId를 설정합니다.
        kdtSessionEvalDTO.setKdtPartId(kdtPartEntity.getKdtPartId());

        // DTO를 Entity로 변환
        KDTSessionEvalEntity kdtSessionEvalEntity = kdtSessionEvalDTO.toEntity(kdtPartEntity);

        // Entity를 DB에 저장
        kdtSessionEvalRepository.save(kdtSessionEvalEntity);

        return 3;  // 성공하였습니다
    }


}