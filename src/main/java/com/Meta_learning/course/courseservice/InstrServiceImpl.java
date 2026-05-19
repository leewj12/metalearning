package com.Meta_learning.course.courseservice;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTInstrSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTCourseEntity.KDTCourseEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import com.Meta_learning.admin.dto.response.InstrCreateResponse;
import com.Meta_learning.course.courseentity.InstrEntity;
import com.Meta_learning.course.courseentity.InstrStatus;
import com.Meta_learning.course.courserepository.InstrRepository;
import com.Meta_learning.course.courseservice.requset.InstrCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstrServiceImpl implements InstrService{

    private final InstrRepository instrRepository;
    private final KDTStaffRepository kdtStaffRepository;

    @Override
    public void createInstrUp(InstrCreateServiceRequest instrCreateServiceRequest) {
        InstrEntity instrEntity = InstrEntity.builder()
                .userEntity(instrCreateServiceRequest.getUserEntity())
                .instrDescript(instrCreateServiceRequest.getInstrDescript())
                .instrCompany(instrCreateServiceRequest.getInstrCompany())
                .instrStatus(InstrStatus.PENDING)
                .build();

        instrRepository.save(instrEntity);


    }

    @Override
    public boolean hasInstrUp(UserEntity user) {
        // InstrRepository에서 userId를 기준으로 신청 여부를 확인
        return instrRepository.existsByUserEntity(user);
    }

    @Override
    public List<InstrCreateResponse> getAllInstrRequests() {
        return instrRepository.findAllByInstrStatus(InstrStatus.PENDING)
                .stream()
                .map(instr -> new InstrCreateResponse(
                        instr.getUserEntity().getName(),
                        instr.getUserEntity().getUserEmail(),
                        instr.getInstrDescript(),
                        instr.getInstrCompany()))
                .toList();
    }

    @Transactional
    @Override
    public void approveInstr(String email) {
        InstrEntity instr = instrRepository.findByUserEntity_UserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("강사 신청을 찾을 수 없습니다."));

        // UserEntity 가져오기
        UserEntity user = instr.getUserEntity();

        // 사용자 역할이 STUDENT이면 INSTRUCTOR로 업데이트
        if ("STUDENT".equals(user.getUserRole())) {
            user.upUserRoleInstr(); // 변경감지를 통해 업데이트
        }

        // InstrStatus를 APPROVED로 업데이트
        instr.updateInstrStatus(InstrStatus.APPROVED);
        // instr도 변경감지를 통해 상태가 업데이트됩니다.
    }

    @Transactional
    @Override
    public void rejectInstr(String email) {
        InstrEntity instr = instrRepository.findByUserEntity_UserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("강사 신청을 찾을 수 없습니다."));

        // InstrStatus를 CANCELED로 업데이트 (변경감지 이용)
        instr.updateInstrStatus(InstrStatus.CANCELED);

    }

    @Override
    public boolean existsByUserId(Long userId) {
        return instrRepository.existsByUserEntity_UserId(userId); // InstrEntity가 존재하는지 확인
    }

    @Override
    public List<KDTInstrSessionDTO> getInstrSessionByUser(Long userId) {
        List<KDTStaffEntity> staffEntities = kdtStaffRepository.findByUserEntityUserId(userId);
        return staffEntities.stream()
                .map(staffEntity -> {
                    KDTSessionEntity session = staffEntity.getKdtSessionEntity();
                    KDTCourseEntity course = session.getKdtCourseEntity();
                    return new KDTInstrSessionDTO(
                            session.getKdtSessionId(),
                            course.getKdtCourseId(),
                            session.getKdtSessionNum(),
                            session.getKdtSessionTitle(),
                            course.getKdtCourseTitle(),
                            session.getKdtSessionStartDate(),
                            session.getKdtSessionEndDate(),
                            session.getKdtSessionCategory(),
                            session.getKdtSessionOnline() != null && session.getKdtSessionOnline()
                            , session.getKdtSessionStatus().getText()
                    );
                })
                .collect(Collectors.toList());
    }
}
