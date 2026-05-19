package com.Meta_learning.KDT.KDTservice.KDTService;


import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionCategoryDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionViewDTO;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface KDTService {

    int kdtcoursesave(KDTCourseDTO kdtCourseDto);

    int kdtsessionsave(KDTSessionDTO kdtSessionDto);

    List<KDTCourseDTO> courseall();

    // courseId에 해당하는 세션 번호와 과정명을 반환
    Map<String, Object> getSessionNumAndCourseTitleByCourseId(Long courseId);

    //국비과정 프라이머리키로 찾는 메서드
    KDTCourseDTO kdtcourseupdate(Long courseId);

    //국비과정 수정하는 메서드
    boolean updateCourse(Long courseId, KDTCourseDTO kdtcourse);

    // 국비과정 삭제하는 메서드
    boolean deleteCourse(Long courseId);

    // 세션이 존재하는지 확인하는 메서드
    boolean hasSessions(Long courseId);

    // 국비과정 아이디로 세션 전체 찾기임
    List<KDTSessionDTO> getSessionsByCourseId(Long courseId);

    //세션 아이디로 세션전체 가져오기
    KDTSessionDTO getSessionsBySessId(Long sessionId);

    //세션아이디로 강사 명단 삭제하는거임
    boolean deleteInstructor(Long kdtSessionId, Long userId);

    //회차 수정 하는 메서드
    Boolean updateSession(Long sessionId, KDTSessionDTO sessionDTO);

    // 모든 회차 정보 찾는 메서드
    List<KDTSessionDTO> getSessionsAll();

    // 제목, 카테고리, 전체 세션을 조건에 맞게 조회
    Page<KDTSessionViewDTO> getSessionsAllview(String searchName, String searchCategory, Pageable pageable);


// 세션 ID로 해당 코스 ID를 찾는 메서드
    Long findCourseIdBySessionId(Long kdtSessionId);


    //국비 비회원 상담신청목록 저장
    boolean consultationsave(KDTAppConsultDTO kdtAppConsultDTO);


    //비회원 상담 신청 삭제하는 메서드임
    boolean deleteConsult(Long consultId, Long sessionId);

    //회차 삭제하는 메서드
    boolean deleteSession(Long sessionId);

    //상담신청 회원들 페이징 처리
    Page<KDTAppConsultDTO> getAppConsultAllview(String searchName, Long sessionId, KDTAppConsultStatus status, Pageable pageable);

    //상담현황 수정하는 메서드
    boolean updateStatus(String sessionId, String consultId, String newStatus);

    //매니저 회차에 속한 사람만 찾는 메서드임
    List<KDTSessionDTO> getManagerSessionsByCourseId(Long courseId, Long userId);

    //카테고리 구현하는 메서드
    List<KDTSessionCategoryDTO> getDistinctCategories();

}
