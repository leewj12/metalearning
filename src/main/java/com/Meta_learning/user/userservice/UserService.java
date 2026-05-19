package com.Meta_learning.user.userservice;


import com.Meta_learning.KDT.KDTDTO.KDTStaffDTO.KDTStaffDTO;
import com.Meta_learning.main.emailservice.EmailVerificationDTO;
import com.Meta_learning.user.userdto.FindUserIdDTO;
import com.Meta_learning.user.userdto.UserIdDTO;
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // 회원정보 저장하는 메서드
    boolean usersave(UserSignUpDTO userSignUpDTO);

    // 회원들 정보 전체 찾는 메서드
    List<UserSignUpDTO> userall();

    // 권한이 매니저인 세션 등록 안댄 정보 찾는 메서드
    List<UserSignUpDTO> usermanagerall(Long sessionId);

    // 권한이 매니저인 세션 등록댄 정보 찾는 메서드
    List<UserSignUpDTO> userRegisteredManager(Long sessionId);

    // 권한이 강사인 모든 정보 찾는 메서드
    List<UserSignUpDTO> userinstrall(Long sessionId);

    // 권한이 강사이며 세션에 등록된 유저 찾는 메서드
    List<UserSignUpDTO> userRegisteredInstructors(Long sessionId);

    // 회차별 매니저나 강사 넣는 메서드
    int instrsave(KDTStaffDTO kdtStaffDTO);

    // 이메일 중복 체크 확인
    boolean findUser(String email);  // 이메일 중복 체크

    // 이메일 인증번호 메서드
    boolean sendVerificationEmail(String email);
    boolean isEmailAvailable(String email);
    boolean verifyEmailCode(EmailVerificationDTO emailVerificationDTO);

    // staffId로 이름 찾기
    String findStaffNameByKdtStaffId(Long kdtStaffId);

    // partId로 이름 찾기
    String findStaffNameByKdtPartId(Long kdtPartId);


//    // 권한이 학생인 회원정보 찾는 메서드 (검색어와 페이지네이션 적용)
//    Page<UserSignUpDTO> findStudentAll(String searchName, Pageable pageable);

    // 학생 검색 (세션에 참여한 유저 제외, 페이징 처리)
    Page<UserSignUpDTO> findStudentAll(String searchName, Pageable pageable, Long sessionId);


    //유저 id 이메일 찾는 메서드
    UserIdDTO findUserByNameBirthAndPhone(FindUserIdDTO FindUserIdDTO);

    //유저 id로 유저 정보 찾는 메서드
    UserSignUpDTO getUserById(Long userId);  // 사용자 ID로 정보를 가져오는 서비스 호출


    //유저 삭제하는 메서드
    boolean deleteUser(Long userId);

    //강사계정 등록하는 메서드
    int instructor(UserSignUpDTO userSignUpDTO);

    //관리자를 제외한 유저 정보 가져오기
    List<UserSignUpDTO> allAdminsExcluded(); // 유저 정보 가져오기

    //관리자를 제외한 유저 정보 상세보기
    UserSignUpDTO getUserByIdNoadmin(Long userId);

    //관리자가 유저 정보 업데이트하는 메서드
    boolean adminUserUpdate(UserSignUpDTO userSignUpDTO);


    //유저 권한 찾는 메서드
    String getUserRole(Long userId);


    //유저 비밀번호가 맞는지 확인하는 테스트
    boolean password(UserPassWordDTO userPassWordDTO);



}
