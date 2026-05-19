package com.Meta_learning.KDT.KDTservice.outline;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseOutlineRepository.KDTCourseOutlineRepository;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineUpdateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTCourseOutlineResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KdtCourseOutlineServiceImpl implements KdtCourseOutlineService {

    private final KDTCourseOutlineRepository kdtCourseOutlineRepository;

    @Override
    public List<KDTCourseOutlineResponse> getKdtCourseOutlineBySessionId(Long kdtSessionId) {
        List<KDTCourseOutlineEntity> entities = kdtCourseOutlineRepository.findByKdtSessionEntityKdtSessionId(kdtSessionId);
        return entities.stream()
                .map(KDTCourseOutlineResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public KDTCourseOutlineResponse createKdtCourseOutline(KDTCourseOutlineCreateServiceRequest request) {
        KDTCourseOutlineEntity kdtCourseOutlineEntity = KDTCourseOutlineEntity.builder()
                .kdtSessionEntity(request.getKdtSessionEntity())
                .kdtCourseOutline(request.getKdtCourseOutline())
                .build();

        KDTCourseOutlineEntity savedEntity = kdtCourseOutlineRepository.save(kdtCourseOutlineEntity);
        return KDTCourseOutlineResponse.of(savedEntity);
    }

    @Override
    public KDTCourseOutlineResponse updateKdtCourseOutline(KDTCourseOutlineUpdateServiceRequest request) {
        KDTCourseOutlineEntity kdtCourseOutlineEntity = KDTCourseOutlineEntity.builder()
                .kdtCourseOutlineId(request.getKdtCourseOutlineId())
                .kdtSessionEntity(request.getKdtSessionEntity())
                .kdtCourseOutline(request.getKdtCourseOutline())
                .build();

        KDTCourseOutlineEntity savedEntity = kdtCourseOutlineRepository.save(kdtCourseOutlineEntity);
        return KDTCourseOutlineResponse.of(savedEntity);
    }

    @Override
    public KDTCourseOutlineResponse getKdtCourseOutlineByKdtCourseOutlineId(Long kdtCourseOutlineId) {
        KDTCourseOutlineEntity kdtCourseOutlineEntity = kdtCourseOutlineRepository.findByKdtCourseOutlineId(kdtCourseOutlineId);
        if (kdtCourseOutlineEntity == null) {
            // 예외를 던지거나 적절한 처리
            throw new EntityNotFoundException("Course outline not found for id " + kdtCourseOutlineId);
        }
        return KDTCourseOutlineResponse.of(kdtCourseOutlineEntity);
    }

    @Override
    public void deleteKdtCourseOutline(Long id) {
        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Outline not found with ID: " + id));
        kdtCourseOutlineRepository.delete(outlineEntity);
    }

//    public KDTCourseOutlineEntity updateKdtCourseOutline(Long id, String newOutline) {
//        KDTCourseOutlineEntity entity = kdtCourseOutlineRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Outline not found with ID: " + id));
//        entity.setKdtCourseOutline(newOutline);
//        return kdtCourseOutlineRepository.save(entity);
//    }

//
//    public KDTCourseOutlineResponse updateKdtCourseOutline(Long id, String newOutline) {
//        KDTCourseOutlineEntity entity = kdtCourseOutlineRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Outline not found with ID: " + id));
//        entity.setKdtCourseOutline(newOutline);
//        KDTCourseOutlineEntity updatedEntity = kdtCourseOutlineRepository.save(entity);
//        return KDTCourseOutlineResponse.of(updatedEntity);
//    }
//
//    public void deleteKdtCourseOutline(Long id) {
//        kdtCourseOutlineRepository.deleteById(id);
//    }
}