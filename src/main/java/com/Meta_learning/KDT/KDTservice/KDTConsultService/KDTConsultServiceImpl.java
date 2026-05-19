package com.Meta_learning.KDT.KDTservice.KDTConsultService;


import com.Meta_learning.KDT.KDTDTO.KDTConsultDTO.KDTConsultDTO;
import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultCategory;
import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultEntity;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTrepository.KDTConsultRepository.KDTConsultRepository;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KDTConsultServiceImpl implements KDTConsultService {
    private final KDTPartRepository kdtPartRepository;
    private final KDTConsultRepository kdtConsultRepository;
    private final UserRepository userRepository;
    @Override
    public Map<Long, String> findRegisteredStudent(Long kdtSessionId) {
        List<KDTPartEntity> kdtPartEntities = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);
        // 결과를 Map 형태로 변환
        Map<Long, String> result = new HashMap<>();
        for (KDTPartEntity kdtPartEntity : kdtPartEntities) {
            result.put(kdtPartEntity.getKdtPartId(), kdtPartEntity.getUserEntity().getName());
        }
        return result;
    }

    @Override
    public KDTConsultEntity kdtConsultSave(KDTConsultDTO kdtConsultDTO) {
        KDTConsultEntity kdtConsultEntity = KDTConsultEntity.builder()
                .kdtConsultId(kdtConsultDTO.getKdtConsultId()) // 기존 ID를 그대로 사용, 만약 새 엔티티라면 ID는 null로 두어야 함
                .userEntity(userRepository.findByUserId(kdtConsultDTO.getUserId()))               // DTO에서 userId를 통해 UserEntity를 찾아야 함
                .kdtPartEntity(kdtPartRepository.findByKdtPartId(kdtConsultDTO.getKdtPartId()))         // DTO에서 kdtPartId를 통해 KDTPartEntity를 찾아야 함
                .kdtConsultTitle(kdtConsultDTO.getKdtConsultTitle())
                .kdtConsultContent(kdtConsultDTO.getKdtConsultContent())
                .kdtConsultCategory(KDTConsultCategory.valueOf(kdtConsultDTO.getKdtConsultCategory()))         // DTO에서 category를 찾거나 직접 변환
                .kdtConsultDate(kdtConsultDTO.getKdtConsultDate())
                .build();
            return kdtConsultRepository.save(kdtConsultEntity);
    }

    @Override
    public KDTConsultDTO findKdtConsultById(Long kdtConsultId) {
        KDTConsultEntity kdtConsultEntity = kdtConsultRepository.findByKdtConsultId(kdtConsultId);
        if(kdtConsultEntity == null) return null;

        KDTConsultDTO kdtConsultDTO = new KDTConsultDTO();

        // 엔티티의 값을 DTO로 복사
        kdtConsultDTO.setKdtConsultId(kdtConsultEntity.getKdtConsultId());
        kdtConsultDTO.setUserId(kdtConsultEntity.getUserEntity().getUserId());
        kdtConsultDTO.setKdtPartId(kdtConsultEntity.getKdtPartEntity().getKdtPartId());
        kdtConsultDTO.setKdtConsultTitle(kdtConsultEntity.getKdtConsultTitle());
        kdtConsultDTO.setKdtConsultContent(kdtConsultEntity.getKdtConsultContent());
        kdtConsultDTO.setKdtConsultCategory(kdtConsultEntity.getKdtConsultCategory().getText());  // Enum을 문자열로 변환
        kdtConsultDTO.setKdtConsultDate(kdtConsultEntity.getKdtConsultDate());
        return kdtConsultDTO;
    }

    @Override
    public void deleteConsult(Long kdtConsultId) {
        kdtConsultRepository.deleteById(kdtConsultId);
    }

    @Override
    // 세션 ID를 기준으로 전체 리스트 조회 (페이징 처리)
    public Page<KDTConsultDTO> findAllWithPaging(Long kdtSessionId, Pageable pageable) {
        Page<KDTConsultEntity> entityPage = kdtConsultRepository.findByKdtPartEntity_KdtSessionEntity_KdtSessionIdOrderByKdtConsultDateDesc(kdtSessionId, pageable);
        // AtomicLong을 사용하여 index 값을 추적
        // `AtomicLong`을 처음에 전체 요소 수와 관련된 값으로 설정하여, 최신 항목이 1로 시작하도록 만듦
        AtomicLong index = new AtomicLong((int) (entityPage.getTotalElements() - (pageable.getPageNumber() * pageable.getPageSize())));

        // Entity 리스트를 DTO 리스트로 변환하면서 index 값을 전달
        List<KDTConsultDTO> dtoList = entityPage.stream()
                .map(entity -> convertToDTO(entity, index.getAndDecrement()))  // index 값을 감소시키며 DTO로 변환
                .collect(Collectors.toList());

        // 변환된 DTO 리스트로 새 Page 생성해서 반환
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    @Override
    // 검색 조건이 있을 때 데이터 조회
    public Page<KDTConsultDTO> searchWithPaging(Long kdtSessionId, String searchCategory, String search, Pageable pageable) {
        Page<KDTConsultEntity> consultEntities = kdtConsultRepository.searchWithPaging(kdtSessionId, searchCategory, search, pageable);

        // AtomicLong을 사용하여 index 값을 추적
        // 전체 데이터의 개수를 기준으로 인덱스를 설정: 첫 페이지에서 가장 큰 인덱스 값
        AtomicLong index = new AtomicLong(consultEntities.getTotalElements() - (pageable.getPageNumber() * pageable.getPageSize()));

        // map을 사용하여 각 항목을 변환하면서 index 값을 1씩 감소시킴
        return consultEntities.map(entity -> convertToDTO(entity, index.getAndDecrement()));
    }

    private KDTConsultDTO convertToDTO(KDTConsultEntity entity, Long index) {
        return new KDTConsultDTO(
                entity.getKdtConsultId(),
                index,
                entity.getUserEntity().getUserId(),
                entity.getUserEntity().getName(),   // 국비 담당자명
                entity.getKdtPartEntity().getKdtPartId(),
                entity.getKdtPartEntity().getUserEntity().getName(), // 참가자명
                entity.getKdtConsultTitle(),
                entity.getKdtConsultContent(),
                entity.getKdtConsultCategory().getText(), // 카테고리는 String으로 변환
                entity.getKdtConsultDate()
        );
    }
}
