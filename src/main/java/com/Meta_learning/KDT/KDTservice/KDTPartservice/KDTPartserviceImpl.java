package com.Meta_learning.KDT.KDTservice.KDTPartservice;


import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartDTO;
import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartStatus;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.user.userdto.UserPartDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
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
public class KDTPartserviceImpl implements KDTPartservice  {

    private final KDTPartRepository kdtPartRepository;
    private final KDTSessionRepository kdtSessionRepository;
    private final UserRepository userRepository;

    // 회차에 등록하는 메서드임
    @Override
    public int studentSessionsSave(KDTPartDTO kdtPartDTO) {

        try {
            // KDTSessionEntity와 UserEntity를 로딩
            KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findById(kdtPartDTO.getKdtSessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Session not found for id " + kdtPartDTO.getKdtSessionId()));

            // 세션의 정원 수와 현재 등록된 인원 수 확인
            int sessionMaxCapacity = kdtSessionEntity.getKdtSessionMaxCapacity(); // 정원
            long currentRegisteredCount = kdtPartRepository.countByKdtSessionEntity_KdtSessionId(kdtPartDTO.getKdtSessionId()); // 현재 등록된 인원 수

            // 현재 등록된 인원 수가 정원을 초과하면 오류 반환
            if (currentRegisteredCount + kdtPartDTO.getUserIds().size() > sessionMaxCapacity) {
                return 2; // 인원이 꽉 찼을 때
            }

            // userIds 리스트에서 각각의 userId를 처리하여 KDTPartEntity로 변환
            List<KDTPartEntity> kdtPartEntities = kdtPartDTO.getUserIds().stream()
                    .map(userId -> {
                        // 유저가 이미 해당 세션에 등록되어 있는지 확인
                        boolean isUserAlreadyRegistered = kdtPartRepository.existsByKdtSessionEntity_KdtSessionIdAndUserEntity_UserId(kdtPartDTO.getKdtSessionId(), userId);

                        if (isUserAlreadyRegistered) {
                            // 이미 등록된 유저가 있다면 4번 코드로 반환
                            return null; // null 값으로 반환해 해당 유저는 처리하지 않도록 합니다.
                        }

                        // 해당 유저가 세션에 등록되지 않았다면, 유저 정보를 가져옴
                        UserEntity userEntity = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found for id " + userId));

                        // 유저의 역할이 학생인지 확인
                        if (!userEntity.getUserRole().equals("STUDENT")) {  // "학생" 역할만 허용
                            return null;  // 학생이 아니라면 null을 반환하여 해당 유저는 처리하지 않도록 함
                        }

                        return KDTPartEntity.builder()
                                .userEntity(userEntity)  // UserEntity 설정
                                .kdtSessionEntity(kdtSessionEntity)  // KDTSessionEntity 설정
                                .kdtPartStatus(KDTPartStatus.WAITING)  // PENDING 상태 설정
                                .kdtPartEmp(false)  // 취업 상태 False 설정
                                .build();
                    })
                    .filter(Objects::nonNull)  // null 필터링 (이미 등록된 유저나 학생이 아닌 유저는 처리하지 않음)
                    .collect(Collectors.toList());

            // 이미 등록된 유저가 있다면 4번을 반환
            if (kdtPartEntities.size() < kdtPartDTO.getUserIds().size()) {
                return 4;  // 이미 등록된 유저가 있을 경우
            }

            // saveAll로 한 번에 저장
            kdtPartRepository.saveAll(kdtPartEntities);

            return 1;  // 성공

        } catch (Exception e) {
            e.printStackTrace();
            return 3; // 실패
        }
    }


    //현재 이 회차에 등록된 학생수 찾는 메서드
    @Override
    public KDTPartTotalDTO studentCountAll(Long sessionId) {
        // KDTSessionId로 학생 수를 조회
        long studentCount = kdtPartRepository.countByKdtSessionEntity_KdtSessionId(sessionId);

        // DTO로 반환
        return new KDTPartTotalDTO((int) studentCount);  // long -> int로 변환하여 반환
    }

    @Override
    public Long findPartIdBySessionIdAndUserId(Long kdtSessionId, Long userId) {
        KDTPartEntity kdtPartEntity = kdtPartRepository.findByKdtSessionEntity_KdtSessionIdAndUserEntity_userId(kdtSessionId, userId);
        return kdtPartEntity.getKdtPartId();
    }

    @Override
    public List<UserPartDTO> userpartall(Long sessionId) {
        // KDTSessionEntity를 기준으로 해당 회차에 참가한 모든 사용자 리스트 조회
        List<KDTPartEntity> kdtPartEntities = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(sessionId);

        // KDTPartEntity 리스트를 UserPartDTO로 변환하여 반환
        return kdtPartEntities.stream()
                .map(kdtPartEntity -> new UserPartDTO(
                        kdtPartEntity.getUserEntity().getUserId(),
                        kdtPartEntity.getKdtPartId(),
                        kdtPartEntity.getKdtSessionEntity().getKdtSessionId(),
                        kdtPartEntity.getUserEntity().getUserEmail(),
                        kdtPartEntity.getUserEntity().getUserRole(),
                        kdtPartEntity.getUserEntity().getName(),
                        kdtPartEntity.getUserEntity().getUserGender(),
                        kdtPartEntity.getUserEntity().getUserBirth(),
                        kdtPartEntity.getUserEntity().getUserPhone(),
                        kdtPartEntity.getUserEntity().getUserAddress(),
                        kdtPartEntity.getUserEntity().getUserEduLevel(),
                        kdtPartEntity.getKdtPartStatus().name(),  // 상태를 String으로 반환
                        kdtPartEntity.getKdtPartEmp()  // 취업 상태
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUserPart(Long sessionId, Long kdtPartId) {
        try {
            // 세션 ID와 파트 ID로 참가자를 찾아 삭제
            Optional<KDTPartEntity> userPart = kdtPartRepository.findByKdtSessionEntity_KdtSessionIdAndKdtPartId(sessionId, kdtPartId);

            if (userPart.isPresent()) {
                // 참가자가 존재하면 삭제
                kdtPartRepository.delete(userPart.get());
                return true;  // 삭제 성공
            } else {
                return false;  // 해당 참가자가 존재하지 않으면 실패
            }
        } catch (Exception e) {
            // 예외 발생 시
            e.printStackTrace();
            return false;  // 예외 발생 시 실패
        }
    }

    @Override
    public boolean updateUserPart(Long sessionId, Long kdtPartId, KDTPartStatus newStatus, Boolean newEmploymentStatus) {
        Optional<KDTPartEntity> partEntityOptional = kdtPartRepository.findById(kdtPartId);

        if (partEntityOptional.isPresent()) {
            KDTPartEntity partEntity = partEntityOptional.get();

            // 상태와 취업 상태를 업데이트하는 메서드 호출
            partEntity.updateFields(newStatus, newEmploymentStatus);

            // 변경된 엔티티 저장
            kdtPartRepository.save(partEntity);
            return true;
        } else {
            return false;
        }
    }

}
