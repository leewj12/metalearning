package com.Meta_learning.manager.managerservice;

import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTCourseEntity.KDTCourseEntity;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseRepository.KDTCourseRepository;
import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerServiceImpl implements ManagerService {  // 인터페이스 구현

    private final KDTStaffRepository kdtStaffRepository;
    private final KDTCourseRepository kdtCourseRepository;

    @Override
    // 해당 사용자가 담당하는 세션의 국비 과정 리스트 가져오기
    public List<KDTCourseDTO> getCoursesByUser(Long userId) {
        // KDTStaffEntity에서 해당 userId가 참여한 세션을 찾기
        List<KDTStaffEntity> staffEntities = kdtStaffRepository.findByUserEntity_UserId(userId);

        // 세션 정보가 없으면 빈 리스트 반환
        if (staffEntities.isEmpty()) {
            return List.of();
        }

        // 세션에 관련된 과정들을 가져오기
        List<KDTCourseEntity> courses = staffEntities.stream()
                .map(staff -> staff.getKdtSessionEntity().getKdtCourseEntity())  // 각 세션에서 과정 찾기
                .distinct()  // 중복된 과정 제거
                .collect(Collectors.toList());

        // DTO로 변환 후 반환
        return courses.stream()
                .map(course -> new KDTCourseDTO(
                        course.getKdtCourseId(),
                        course.getKdtCourseTitle(),
                        course.getKdtCourseStatus(),
                        course.getKdtCourseType(),
                        course.getKdtCourseCreatedAt(),
                        course.getKdtCourseUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 유저 ID로 해당 유저가 배정된 KDTSessionEntity 정보를 가져옵니다.
    @Override
    public List<KDTSessionDTO> getSessionsByUserId(Long userId) {
        // 유저 ID로 해당 유저가 배정된 KDTSessionEntity 정보를 가져옵니다.
        List<KDTStaffEntity> staffEntities = kdtStaffRepository.findByUserEntity_UserId(userId);

        // 세션 정보가 없으면 빈 리스트 반환
        if (staffEntities.isEmpty()) {
            return List.of();  // 빈 리스트 반환
        }

        // 각 세션에 대해 필요한 정보를 DTO로 변환하여 반환
        List<KDTSessionDTO> getSessions = staffEntities.stream()
                .map(staff -> staff.getKdtSessionEntity())  // KDTStaffEntity에서 KDTSessionEntity 가져오기
                .distinct()  // 중복된 세션을 제거
                .map(KDTSessionDTO::convertSessionEntityToDTO)  // KDTSessionEntity -> KDTSessionDTO 변환
                .collect(Collectors.toList());

        // 세션 목록 반환
        return getSessions;
    }

}
