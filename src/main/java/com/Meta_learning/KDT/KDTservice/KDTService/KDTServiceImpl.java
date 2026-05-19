package com.Meta_learning.KDT.KDTservice.KDTService;


import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionCategoryDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionViewDTO;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsult;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus;
import com.Meta_learning.KDT.KDTentity.KDTCourseEntity.KDTCourseEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionStatus;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.KDT.KDTrepository.KDTAppConsultRepository.KDTAppConsultRepository;
import com.Meta_learning.KDT.KDTrepository.KDTCourseRepository.KDTCourseRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KDTServiceImpl implements KDTService {

    private final KDTCourseRepository kdtCourseRepository;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTStaffRepository kdtStaffRepository;
    private final KDTAppConsultRepository kdtAppConsultRepository;


    //국비과정 등록하는 메서드임
    @Override
    public int kdtcoursesave(KDTCourseDTO kdtCourseDto) {
        try {
            // 중복 확인 사용하는거임
            boolean exists = kdtCourseRepository.existsByKdtCourseTitle(kdtCourseDto.getKdtCourseTitle());
            if (exists) {
                return 2;
            }
            KDTCourseEntity kdtCourseEntity = KDTCourseEntity.builder()
                    .kdtCourseTitle(kdtCourseDto.getKdtCourseTitle())
                    .kdtCourseStatus(kdtCourseDto.getKdtCourseStatus())
                    .kdtCourseType(kdtCourseDto.getKdtCourseType())
                    .build();
            kdtCourseRepository.save(kdtCourseEntity);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //회차 등록하는 메서드임
    @Override
    public int kdtsessionsave(KDTSessionDTO kdtSessionDto) {
        try {
            // 코스 ID와 세션 번호를 기준으로 이미 존재하는 세션이 있는지 확인
            boolean exists = kdtSessionRepository.existsByKdtCourseEntity_KdtCourseIdAndKdtSessionNum(
                    kdtSessionDto.getKdtCourseId(), kdtSessionDto.getKdtSessionNum());

            if (exists) {
                return 2; // 이미 세션 번호가 존재하는 경우
            }
            // 세션 엔티티 생성
            KDTSessionEntity kdtSessionEntity = KDTSessionEntity.builder()
                    .kdtCourseEntity(KDTCourseEntity.builder().kdtCourseId(kdtSessionDto.getKdtCourseId()).build()) // 관계 설정
                    .kdtSessionNum(kdtSessionDto.getKdtSessionNum())
                    .kdtSessionTitle(kdtSessionDto.getKdtSessionTitle())
                    .kdtSessionDescript(kdtSessionDto.getKdtSessionDescript())
                    .kdtSessionStartDate(kdtSessionDto.getKdtSessionStartDate())
                    .kdtSessionEndDate(kdtSessionDto.getKdtSessionEndDate())
                    .kdtSessionCategory(kdtSessionDto.getKdtSessionCategory())
                    .kdtSessionMaxCapacity(kdtSessionDto.getKdtSessionMaxCapacity())
                    .kdtSessionThumbnail(kdtSessionDto.getKdtSessionThumbnail())
                    .kdtSessionStartTime(kdtSessionDto.getKdtSessionStartTime())
                    .kdtSessionEndTime(kdtSessionDto.getKdtSessionEndTime())
                    .kdtSessionPostcode(kdtSessionDto.getKdtSessionPostcode())
                    .kdtSessionAddress(kdtSessionDto.getKdtSessionAddress())
                    .kdtSessionAddressDetail(kdtSessionDto.getKdtSessionAddressDetail())
                    .kdtSessionOnline(kdtSessionDto.getKdtSessionOnline())
                    .kdtSessionTotalDay(kdtSessionDto.getKdtSessionTotalDay())  // 추가된 필드
                    .kdtSessionOnedayTime(kdtSessionDto.getKdtSessionOnedayTime())  // 추가된 필드
                    .kdtSessionTotalTime(kdtSessionDto.getKdtSessionTotalTime())  // 추가된 필드
                    .kdtSessionStatus(KDTSessionStatus.valueOf(kdtSessionDto.getKdtSessionStatus())) // 상태 추가
                    .build();

            //

            // 세션 엔티티 저장
            kdtSessionRepository.save(kdtSessionEntity);

            return 1; // 성공적으로 저장된 경우
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // 예외 발생 시 실패
        }
    }

    //KDT 교육과정명 전체찾는 메서드임 =  courseall  중복해서 사용하면 댐
    @Override
    public List<KDTCourseDTO> courseall() {
        List<KDTCourseEntity> courses = kdtCourseRepository.findAll();
        List<KDTCourseDTO> courseall = courses.stream()
                .map(course -> new KDTCourseDTO(
                        course.getKdtCourseId(),
                        course.getKdtCourseTitle(),
                        course.getKdtCourseStatus(),
                        course.getKdtCourseType(),
                        course.getKdtCourseCreatedAt(),
                        course.getKdtCourseUpdatedAt()
                ))
                .collect(Collectors.toList());
        return courseall;
    }


    //과정명 선택화면 회차 찾고 과정명 찾아옴
    @Override
    public Map<String, Object> getSessionNumAndCourseTitleByCourseId(Long courseId) {
        try {
            // courseId에 해당하는 마지막 세션을 찾기 (세션 번호가 큰 순서대로)
            Optional<KDTSessionEntity> lastSession = kdtSessionRepository
                    .findTopByKdtCourseEntity_KdtCourseIdOrderByKdtSessionNumDesc(courseId);

            return lastSession.map(session -> {
                Map<String, Object> result = new HashMap<>();
                result.put("sessionNum", session.getKdtSessionNum());  // 세션 번호
                result.put("courseTitle", session.getKdtCourseEntity().getKdtCourseTitle());  // 과정명
                return result;
            }).orElseGet(() -> {
                // 세션이 없으면 courseId로 과정명을 찾아서 반환
                Optional<KDTCourseEntity> course = kdtCourseRepository.findById(courseId);
                Map<String, Object> result = new HashMap<>();
                result.put("sessionNum", 0);  // 세션 번호는 0
                result.put("courseTitle", course.map(KDTCourseEntity::getKdtCourseTitle).orElse("과정명 없음"));  // 과정명
                return result;
            });
        } catch (Exception e) {
            // 예외 발생 시 로깅
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("sessionNum", 0);  // 예외가 발생한 경우 기본값 0 반환
            result.put("courseTitle", "없음");  // 기본 과정명
            return result;
        }
    }

    //국비과정 수정 메서드
    @Override
    public boolean updateCourse(Long courseId, KDTCourseDTO kdtcourseDto) {
        Optional<KDTCourseEntity> optionalCourse = kdtCourseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            KDTCourseEntity course = optionalCourse.get();
            course.update(kdtcourseDto);
            kdtCourseRepository.save(course);

            return true;
        } else {
            return false;
        }

    }

    // 국비과정 프라이머키로 찾기
    @Override
    public KDTCourseDTO kdtcourseupdate(Long courseId) {
        // 1. courseId에 해당하는 과정 조회
        Optional<KDTCourseEntity> optionalCourse = kdtCourseRepository.findById(courseId);

        // 과정이 존재하지 않으면 예외 처리
        if (!optionalCourse.isPresent()) {
            throw new RuntimeException("해당 과정이 존재하지 않습니다.");
        }
        // Optional을 사용하여 DTO로 변환 후 변수에 담기
        KDTCourseDTO dto = optionalCourse
                .map(entity -> new KDTCourseDTO(
                        entity.getKdtCourseId(),
                        entity.getKdtCourseTitle(),
                        entity.getKdtCourseStatus(),
                        entity.getKdtCourseType(),
                        entity.getKdtCourseCreatedAt(),
                        entity.getKdtCourseUpdatedAt()
                ))
                .orElseThrow(() -> new RuntimeException("해당 과정이 존재하지 않습니다.")); // Optional이 비어 있을 경우 예외 처리

        // 변수를 리턴
        return dto;
    }

    // 국비과정 삭제하는 메서드
    @Override
    public boolean deleteCourse(Long courseId) {
        Optional<KDTCourseEntity> courseOptional = kdtCourseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            return false; // 과정이 존재하지 않으면 삭제할 수 없음
        }

        // 과정에 연관된 세션이 있다면 삭제 불가
        boolean hasSessions = kdtSessionRepository.existsByKdtCourseEntity_KdtCourseId(courseId);

        if (hasSessions) {
            return false; // 세션이 존재하면 삭제할 수 없음
        }

        // 세션이 없으면 삭제
        KDTCourseEntity course = courseOptional.get();
        kdtCourseRepository.delete(course);

        return true; // 삭제 성공
    }

    // 국비과정 프라이머리키로 모든 세션 아이디를 찾아오는 메서드임
    @Override
    public boolean hasSessions(Long courseId) {
        return kdtSessionRepository.existsByKdtCourseEntity_KdtCourseId(courseId); // 세션이 있으면 true 반환
    }

    //과정 아이디로 세션 정보 가져오는거임
    @Override
    public List<KDTSessionDTO> getSessionsByCourseId(Long courseId) {
        // courseId에 해당하는 세션 데이터를 DB에서 가져옴
        List<KDTSessionEntity> sessionEntities = kdtSessionRepository.findByKdtCourseEntity_KdtCourseId(courseId);

        // 가져온 세션 엔티티 리스트를 DTO로 변환
        List<KDTSessionDTO> sessionAll = sessionEntities.stream()
                .map(entity -> {
                    KDTSessionDTO dto = new KDTSessionDTO();
                    dto.setKdtSessionId(entity.getKdtSessionId());
                    dto.setKdtCourseId(entity.getKdtCourseEntity().getKdtCourseId());
                    dto.setKdtSessionNum(entity.getKdtSessionNum());
                    dto.setKdtSessionTitle(entity.getKdtSessionTitle());
                    dto.setKdtSessionDescript(entity.getKdtSessionDescript());
                    dto.setKdtSessionStartDate(entity.getKdtSessionStartDate());
                    dto.setKdtSessionEndDate(entity.getKdtSessionEndDate());
                    dto.setKdtSessionCategory(entity.getKdtSessionCategory());
                    dto.setKdtSessionMaxCapacity(entity.getKdtSessionMaxCapacity());
                    dto.setKdtSessionThumbnail(entity.getKdtSessionThumbnail());
                    dto.setKdtSessionStartTime(entity.getKdtSessionStartTime());
                    dto.setKdtSessionEndTime(entity.getKdtSessionEndTime());
                    dto.setKdtSessionPostcode(entity.getKdtSessionPostcode());
                    dto.setKdtSessionAddress(entity.getKdtSessionAddress());
                    dto.setKdtSessionAddressDetail(entity.getKdtSessionAddressDetail());
                    dto.setKdtSessionOnline(entity.getKdtSessionOnline());
                    dto.setKdtSessionTotalDay(entity.getKdtSessionTotalDay());
                    dto.setKdtSessionOnedayTime(entity.getKdtSessionOnedayTime());
                    dto.setKdtSessionTotalTime(entity.getKdtSessionTotalTime());
                    dto.setKdtSessionStatus(entity.getKdtSessionStatus().getText()); // getText() 메서드를 사용하여 String으로 변환
                    return dto;
                })
                .collect(Collectors.toList());  // DTO 리스트로 변환 후 반환

        // 변환된 sessionAll 반환
        return sessionAll;
    }

    //세션아이디로 단일객체 정보 가져오기
    @Override
    public KDTSessionDTO getSessionsBySessId(Long sessionId) {
        // sessionId로 KDTSessionEntity를 조회하고 Optional로 감싸 반환
        Optional<KDTSessionEntity> entityOpt = kdtSessionRepository.findById(sessionId);

        // 엔티티가 없다면 null을 반환
        if (!entityOpt.isPresent()) {
            return null;
        }

        KDTSessionEntity entity = entityOpt.get(); // 엔티티 객체 가져오기

        // KDTSessionDTO 객체 생성
        KDTSessionDTO dto = new KDTSessionDTO();

        // 엔티티 데이터를 DTO로 매핑
        dto.setKdtSessionId(entity.getKdtSessionId());
        dto.setKdtCourseId(entity.getKdtCourseEntity().getKdtCourseId());
        dto.setKdtSessionNum(entity.getKdtSessionNum());
        dto.setKdtSessionTitle(entity.getKdtSessionTitle());
        dto.setKdtSessionDescript(entity.getKdtSessionDescript());
        dto.setKdtSessionStartDate(entity.getKdtSessionStartDate());
        dto.setKdtSessionEndDate(entity.getKdtSessionEndDate());
        dto.setKdtSessionCategory(entity.getKdtSessionCategory());
        dto.setKdtSessionMaxCapacity(entity.getKdtSessionMaxCapacity());
        dto.setKdtSessionThumbnail(entity.getKdtSessionThumbnail());
        dto.setKdtSessionStartTime(entity.getKdtSessionStartTime());
        dto.setKdtSessionEndTime(entity.getKdtSessionEndTime());
        dto.setKdtSessionPostcode(entity.getKdtSessionPostcode());
        dto.setKdtSessionAddress(entity.getKdtSessionAddress());
        dto.setKdtSessionAddressDetail(entity.getKdtSessionAddressDetail());
        dto.setKdtSessionOnline(entity.getKdtSessionOnline());
        dto.setKdtSessionTotalDay(entity.getKdtSessionTotalDay());
        dto.setKdtSessionOnedayTime(entity.getKdtSessionOnedayTime());
        dto.setKdtSessionTotalTime(entity.getKdtSessionTotalTime());
        dto.setKdtSessionStatus(entity.getKdtSessionStatus().getText()); // getText() 메서드를 사용하여 String으로 변환

        return dto;
    }

    // 강사 및 매니저 삭제임
    @Override
    public boolean deleteInstructor(Long kdtSessionId, Long userId) {
        // 세션 ID와 사용자 ID로 강사를 찾기
        KDTStaffEntity kdtStaffEntity = kdtStaffRepository.findByUserEntityUserIdAndKdtSessionEntityKdtSessionId(userId, kdtSessionId)
                .orElse(null); // 강사를 찾을 수 없으면 null 반환

        if (kdtStaffEntity != null) {
            // 강사 삭제
            kdtStaffRepository.delete(kdtStaffEntity);
            return true;
        } else {
            // 강사를 찾을 수 없으면 false 반환
            return false;
        }
    }

    // 세션 업데이트 메서드
    @Override
    public Boolean updateSession(Long sessionId, KDTSessionDTO sessionDTO) {
        try {
            // 세션 ID로 세션 찾기
            KDTSessionEntity sessionEntity = kdtSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다."));

            // 세션 정보 업데이트
            sessionEntity.updateSessionDetails(sessionDTO);

            // 수정된 세션 저장
            kdtSessionRepository.save(sessionEntity);

            return true; // 성공적으로 업데이트된 경우 true 반환
        } catch (EntityNotFoundException e) {
            // 세션을 찾을 수 없는 경우
            // 로그를 기록하거나 다른 처리를 할 수 있습니다.
            System.err.println("세션을 찾을 수 없습니다. ID: " + sessionId);
            throw new EntityNotFoundException("세션을 찾을 수 없습니다.");
        } catch (Exception e) {
            // 다른 예외 처리 (예: DB 저장 실패 등)
            // 여기서 로그를 기록하거나 실패한 이유를 추적할 수 있습니다.
            System.err.println("세션 업데이트 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("세션 업데이트 실패", e);  // 일반적인 예외 발생
        }
    }

    //kdt 모든 회차정보 가져오는 메서드임
    @Override
    public List<KDTSessionDTO> getSessionsAll() {
        // 엔티티 리스트를 가져와서
        List<KDTSessionEntity> entities = kdtSessionRepository.findAll();

        // 스트림을 사용해 변환 후 리스트로 반환
        return entities.stream()
                .map(KDTSessionDTO::convertSessionEntityToDTO)  // 엔티티를 DTO로 변환
                .collect(Collectors.toList());  // 리스트로 변환하여 반환
    }

    @Override
    public Page<KDTSessionViewDTO> getSessionsAllview(String searchName, String searchCategory, Pageable pageable) {
        // 제목과 카테고리가 모두 있을 경우
        if (!searchName.isEmpty() && !searchCategory.isEmpty()) {
            return kdtSessionRepository.findByKdtSessionTitleContainingAndKdtSessionCategoryContaining(searchName, searchCategory, pageable)
                    .map(KDTSessionViewDTO::convertSessionEntityToViewDTO);  // 엔티티를 DTO로 변환
        }
        // 제목만 있을 경우
        else if (!searchName.isEmpty()) {
            return kdtSessionRepository.findByKdtSessionTitleContaining(searchName, pageable)
                    .map(KDTSessionViewDTO::convertSessionEntityToViewDTO);  // 엔티티를 DTO로 변환
        }
        // 카테고리만 있을 경우
        else if (!searchCategory.isEmpty()) {
            return kdtSessionRepository.findByKdtSessionCategoryContaining(searchCategory, pageable)
                    .map(KDTSessionViewDTO::convertSessionEntityToViewDTO);  // 엔티티를 DTO로 변환
        }
        // 제목과 카테고리 모두 없을 경우 (전체 조회)
        else {
            return kdtSessionRepository.findAll(pageable)
                    .map(KDTSessionViewDTO::convertSessionEntityToViewDTO);  // 엔티티를 DTO로 변환
        }
    }

    //세션아이디로 코스아이디 찾는 메서드
    @Override
    public Long findCourseIdBySessionId(Long kdtSessionId) {
        KDTSessionEntity kdtSession = kdtSessionRepository.findByKdtSessionId(kdtSessionId);
        if (kdtSession != null) {
            // 세션이 존재하면 해당 세션에 연관된 코스 ID 반환
            return kdtSession.getKdtCourseEntity().getKdtCourseId();
        } else {
            throw new RuntimeException("Session not found for ID: " + kdtSessionId);
        }
    }

    // 신청상담일지 저장 메서드
    @Override
    public boolean consultationsave(KDTAppConsultDTO kdtAppConsultDTO) {
        try {
            // 세션 ID로 KDTSessionEntity 조회
            KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findByKdtSessionId(kdtAppConsultDTO.getKdtSessionId());

            // 세션이 존재하지 않으면 false 반환
            if (kdtSessionEntity == null) {
                System.out.println("세션이 존재하지 않습니다. 세션 ID: " + kdtAppConsultDTO.getKdtSessionId());
                return false;  // 세션이 없으면 저장하지 않고 false 반환
            }

            // DTO를 엔티티로 변환
            KDTAppConsult kdtAppConsult = KDTAppConsult.builder()
                    .kdtSessionEntity(kdtSessionEntity)  // 실제 세션 엔티티 설정
                    .kdtAppConsultName(kdtAppConsultDTO.getKdtAppConsultName()) // 신청자 이름
                    .kdtAppConsultGender(kdtAppConsultDTO.getKdtAppConsultGender()) // 신청자 성별
                    .kdtAppConsultBirth(kdtAppConsultDTO.getKdtAppConsultBirth()) // 신청자 생년월일
                    .kdtAppConsultPhone(kdtAppConsultDTO.getKdtAppConsultPhone()) // 신청자 전화번호
                    .kdtAppConsultEmail(kdtAppConsultDTO.getKdtAppConsultEmail()) // 신청자 이메일
                    .kdtAppConsultMotiv(kdtAppConsultDTO.getKdtAppConsultMotiv()) // 동기 (optional)
                    .kdtAppConsultCard(kdtAppConsultDTO.getKdtAppConsultCard()) // 국민내일배움카드 여부
                    .kdtAppConsultAppPath(kdtAppConsultDTO.getKdtAppConsultAppPath()) // 신청 경로
                    .kdtAppConsultEduLevel(kdtAppConsultDTO.getKdtAppConsultEduLevel()) // 학력 수준
                    .kdtAppConsultPrivacyAgree(kdtAppConsultDTO.getKdtAppConsultPrivacyAgree()) // 개인정보 동의 여부
                    .kdtAppConsultMarketingAgree(kdtAppConsultDTO.getKdtAppConsultMarketingAgree()) // 마케팅 동의 여부
                    .kdtAppConsultStatus(KDTAppConsultStatus.PENDING)  // 기본 상태를 PENDING으로 설정
                    .kdtAppCreatedAt(kdtAppConsultDTO.getKdtAppCreatedAt()) // 상담 생성일
                    .build(); // 빌더 패턴을 사용하여 엔티티 객체 생성

            // 변환된 엔티티를 DB에 저장
            kdtAppConsultRepository.save(kdtAppConsult);

            return true;  // 저장 성공 시 true 반환
        } catch (Exception e) {
            // 예외 발생 시 로깅 후 false 반환
            System.err.println("저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();  // 예외의 상세 내용을 콘솔에 출력
            return false;  // 저장 실패 시 false 반환
        }
    }


    //상담일지 삭제하는 메서드임
    @Override
    public boolean deleteConsult(Long consultId, Long sessionId) {
        // 세션이 존재하는지 확인
        if (!kdtSessionRepository.existsById(sessionId)) {
            return false; // 세션이 존재하지 않으면 삭제 실패
        }

        // 해당 상담을 찾기
        KDTAppConsult consult = kdtAppConsultRepository.findById(consultId).orElse(null);

        if (consult != null && consult.getKdtSessionEntity().getKdtSessionId().equals(sessionId)) {
            // 세션이 일치하면 삭제
            kdtAppConsultRepository.delete(consult);
            return true;
        } else {
            // 상담이 존재하지 않거나 세션 ID가 일치하지 않으면 삭제 실패
            return false;
        }
    }


    //회차 삭제하는 메서드임
    @Override
    public boolean deleteSession(Long sessionId) {
        try {
            // 세션 ID로 해당 세션 조회
            KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findByKdtSessionId(sessionId);

            // 세션이 존재하지 않으면 예외 발생
            if (kdtSessionEntity == null) {
                throw new IllegalArgumentException("Session with ID " + sessionId + " not found.");
            }

            // 세션 자체 삭제
            kdtSessionRepository.delete(kdtSessionEntity);

            return true;  // 삭제 성공
        } catch (Exception e) {
            // 예외 발생 시 로깅 후 false 반환
            return false;  // 삭제 실패 시 false 반환
        }
    }

    @Override
    public Page<KDTAppConsultDTO> getAppConsultAllview(String searchName, Long sessionId, KDTAppConsultStatus status, Pageable pageable) {
        Page<KDTAppConsult> page;

        if (searchName != null && !searchName.isEmpty() && status != null) {
            // 이름과 상태를 기준으로 필터링
            page = kdtAppConsultRepository.findByKdtAppConsultNameContainingAndKdtSessionEntity_KdtSessionIdAndKdtAppConsultStatus(
                    searchName, sessionId, status, pageable);
        } else if (searchName != null && !searchName.isEmpty()) {
            // 이름만 기준으로 필터링 (status 제외)
            page = kdtAppConsultRepository.findByKdtAppConsultNameContainingAndKdtSessionEntity_KdtSessionId(
                    searchName, sessionId, pageable);
        } else if (status != null) {
            // 상태만 기준으로 필터링
            page = kdtAppConsultRepository.findByKdtSessionEntity_KdtSessionIdAndKdtAppConsultStatus(sessionId, status, pageable);
        } else {
            // 이름, 상태 없이 세션 ID만 기준으로 필터링
            page = kdtAppConsultRepository.findByKdtSessionEntity_KdtSessionId(sessionId, pageable);
        }

        // 엔티티 리스트를 DTO 리스트로 변환
        Page<KDTAppConsultDTO> dtoPage = page.map(KDTAppConsultDTO::convertEntityToDTO);
        return dtoPage;
    }





// 국비신청 상담 요청한 사람들 상태 수정
    @Override
    public boolean updateStatus(String sessionId, String consultId, String newStatus) {
        // 상태 값이 유효한지 확인 (enum 값으로 변환)
        KDTAppConsultStatus status = validateStatus(newStatus);
        if (status == null) {
            // 유효하지 않은 상태 값이므로 false 반환
            return false;
        }

        // sessionId와 consultId를 Long으로 변환
        Long sessionIdLong;
        Long consultIdLong;

        try {
            sessionIdLong = Long.parseLong(sessionId);
            consultIdLong = Long.parseLong(consultId);
        } catch (NumberFormatException e) {
            return false; // sessionId 또는 consultId가 숫자가 아니면 false 반환
        }

        // 상담 정보를 찾는다
        Optional<KDTAppConsult> consultOpt = kdtAppConsultRepository.findById(consultIdLong);

        if (consultOpt.isPresent()) {
            KDTAppConsult consult = consultOpt.get();

            // 상태 변경
            consult.setStatus(status);

            // 상태 수정 후 저장
            kdtAppConsultRepository.save(consult);
            return true;
        }
        return false; // 상담이 없으면 false 반환
    }

    @Override
    public List<KDTSessionDTO> getManagerSessionsByCourseId(Long courseId, Long userId) {
        // 1. 주어진 과정 ID에 해당하는 모든 세션들을 조회
        List<KDTSessionEntity> sessions = kdtSessionRepository.findByKdtCourseEntity_KdtCourseId(courseId);

        // 2. 각 세션에 대해 해당 유저가 배정되었는지 확인
        List<KDTSessionDTO> filteredSessions = sessions.stream()
                .filter(session -> {
                    // 유저가 해당 세션에 배정되었는지 확인
                    return kdtStaffRepository.existsByUserEntity_UserIdAndKdtSessionEntity_KdtSessionId(userId, session.getKdtSessionId());
                })
                .map(KDTSessionDTO::convertSessionEntityToDTO)  // 엔티티를 DTO로 변환
                .collect(Collectors.toList());

        // 3. 필터링된 세션 반환
        return filteredSessions;
    }

    //회차 카테고리 구하는 메서드
    @Override
    public List<KDTSessionCategoryDTO> getDistinctCategories() {
        // 리포지토리에서 중복되지 않는 카테고리 목록을 가져옴
        List<String> categories = kdtSessionRepository.findDistinctCategories();

        // 카테고리 문자열을 DTO로 변환하여 반환
        return categories.stream()
                .map(category -> new KDTSessionCategoryDTO(category))
                .collect(Collectors.toList());
    }


    // 상태 값이 유효한지 확인하는 메서드
    private KDTAppConsultStatus validateStatus(String status) {
        try {
            return KDTAppConsultStatus.valueOf(status); // 문자열을 Enum으로 변환
        } catch (IllegalArgumentException e) {
            // Enum에 해당하는 값이 없으면 null 반환
            return null;
        }
    }

}