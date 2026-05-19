package com.Meta_learning.user.userservice;


import com.Meta_learning.KDT.KDTDTO.KDTStaffDTO.KDTStaffDTO;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTentity.KDTStaffEntity.KDTStaffEntity;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTrepository.KDTStaffRepository.KDTStaffRepository;
import com.Meta_learning.main.emailservice.EmailService;
import com.Meta_learning.main.emailservice.EmailVerificationDTO;
import com.Meta_learning.user.userdto.FindUserIdDTO;
import com.Meta_learning.user.userdto.UserIdDTO;
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userentity.UserStatus;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KDTStaffRepository kdtStaffRepository;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTPartRepository kdtPartRepository;
    private final EmailService emailService;  // EmailService 주입


    @Override
    public boolean usersave(UserSignUpDTO userSignUpDTO) {
        try {
            // 이메일 중복 체크
            if (userRepository.findByUserEmail(userSignUpDTO.getUserEmail()).isPresent()) {
                return false;  // 이미 사용 중인 이메일이라면 회원가입 실패
            }

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(userSignUpDTO.getUserPw());

            // UserEntity 객체 생성 후 저장
            UserEntity userEntity = UserEntity.builder()
                    .userEmail(userSignUpDTO.getUserEmail()) // 이메일
                    .userPw(encodedPassword) // 암호화된 비밀번호
                    .userRole("STUDENT")  // 기본 역할은 "STUDENT"
                    .name(userSignUpDTO.getName()) // 이름
                    .userGender(userSignUpDTO.getUserGender()) // 성별
                    .userBirth(userSignUpDTO.getUserBirth()) // 생일
                    .userPhone(userSignUpDTO.getUserPhone()) // 전화번호
                    .userPostcode(userSignUpDTO.getUserPostcode()) // 우편번호
                    .userAddress(userSignUpDTO.getUserAddress())
                    .userAddressDetail(userSignUpDTO.getUserAddressDetail())
                    .userEduLevel(userSignUpDTO.getUserEduLevel())
                    .userMarketingAgree(userSignUpDTO.getUserMarketingAgree()) // 마케팅 동의
                    .userPrivacyAgree(userSignUpDTO.getUserPrivacyAgree()) // 개인정보 동의
                    .userStatus(UserStatus.ACTIVE)  // 기본 상태는 "ACTIVE"
                    .build();

            // UserEntity 저장
            userRepository.save(userEntity);

            return true;  // 회원가입 성공
        } catch (Exception e) {
            // 예외 발생 시 오류 로그를 남기고 false 반환
            e.printStackTrace();  // 로깅을 대신하여 스택 트레이스를 출력
            return false;  // 회원가입 실패
        }
    }

    //모든 회원 정보 가져오기
    @Override
    public List<UserSignUpDTO> userall() {
        List<UserEntity> userEntities = userRepository.findAll();

        // UserEntity 리스트를 UserSignUpDTO 리스트로 변환
        List<UserSignUpDTO> userall = userEntities.stream()
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환

                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return userall;
    }

    //권한이 매니저인 사람만 찾기
    @Override
    public List<UserSignUpDTO> usermanagerall(Long sessionId) {
        // 'MANAGER' 역할을 가진 사용자만 조회
        List<UserEntity> userEntities = userRepository.findByUserRole("MANAGER");

        // 해당 세션에 이미 등록된 강사들의 userId를 조회
        List<Long> alreadyRegisteredUserIds = kdtStaffRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(kdtStaff -> kdtStaff.getUserEntity().getUserId())
                .collect(Collectors.toList());

        // 이미 등록되지 않은 강사만 필터링
        List<UserSignUpDTO> userall = userEntities.stream()
                .filter(user -> !alreadyRegisteredUserIds.contains(user.getUserId()))  // 등록되지 않은 강사만 필터링
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환
                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return userall;
    }

    //권한이 매니저이면서 세션에 등록된 사람만 찾기
    @Override
    public List<UserSignUpDTO> userRegisteredManager(Long sessionId) {
        // 'MANAGER' 역할을 가진 사용자들만 조회
        List<UserEntity> userEntities = userRepository.findByUserRole("MANAGER");

        // 해당 세션에 이미 등록된 매너저들의의 userId를 조회
        List<Long> alreadyRegisteredUserIds = kdtStaffRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(kdtStaff -> kdtStaff.getUserEntity().getUserId())
                .collect(Collectors.toList());

        // 이미 등록된 매니저만 필터링
        List<UserSignUpDTO> registeredmanager = userEntities.stream()
                .filter(user -> alreadyRegisteredUserIds.contains(user.getUserId()))  // 등록된 매니저만
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환
                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return registeredmanager;
    }

    //권한이 강사이면서 세션에 등록된 안된  사람만 찾기
    @Override
    public List<UserSignUpDTO> userinstrall(Long sessionId) {
        // 'INSTRUCTOR' 역할을 가진 사용자들만 조회
        List<UserEntity> userEntities = userRepository.findByUserRole("INSTRUCTOR");

        // 해당 세션에 이미 등록된 강사들의 userId를 조회
        List<Long> alreadyRegisteredUserIds = kdtStaffRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(kdtStaff -> kdtStaff.getUserEntity().getUserId())
                .collect(Collectors.toList());

        // 이미 등록되지 않은 강사만 필터링
        List<UserSignUpDTO> userall = userEntities.stream()
                .filter(user -> !alreadyRegisteredUserIds.contains(user.getUserId()))  // 등록되지 않은 강사만 필터링
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환

                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return userall;
    }

    //강사이면서 세션이미 등록된 사람만 찾는거임
    @Override
    public List<UserSignUpDTO> userRegisteredInstructors(Long sessionId) {
        // 'INSTRUCTOR' 역할을 가진 사용자들만 조회
        List<UserEntity> userEntities = userRepository.findByUserRole("INSTRUCTOR");

        // 해당 세션에 이미 등록된 강사들의 userId를 조회
        List<Long> alreadyRegisteredUserIds = kdtStaffRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(kdtStaff -> kdtStaff.getUserEntity().getUserId())
                .collect(Collectors.toList());

        // 이미 등록된 강사만 필터링
        List<UserSignUpDTO> registeredInstructors = userEntities.stream()
                .filter(user -> alreadyRegisteredUserIds.contains(user.getUserId()))  // 등록된 강사만 필터링
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환

                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return registeredInstructors;
    }


    @Override
    public int instrsave(KDTStaffDTO kdtStaffDTO) {
        // 세션 ID가 유효한지 체크
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtStaffDTO.getKdtSessionId())
                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));

        // 강사 및 매니저들을 저장할 리스트
        List<KDTStaffEntity> kdtStaffEntities = new ArrayList<>();

        // 강사 및 매니저 ID 목록을 순회하며 강사 등록 처리
        for (Long userId : kdtStaffDTO.getUserIds()) {
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다."));

            // 해당 세션과 강사 조합 및 매니저가 이미 존재하는지 확인
            boolean alreadyExists = kdtStaffRepository.existsByKdtSessionEntityAndUserEntity(sessionEntity, userEntity);

            if (alreadyExists) {
                // 이미 존재하는 경우, 강사 등록을 하지 않고 skip
                return 2;  // 이미 등록된 경우, 2를 반환 (중복된 강사)
            }

            // 강사 및 매니저 등록을 위한 엔티티 생성
            KDTStaffEntity kdtStaffEntity = KDTStaffEntity.builder()
                    .kdtSessionEntity(sessionEntity)
                    .userEntity(userEntity)
                    .build();

            // 생성된 매니저 및 강사 엔티티를 리스트에 추가
            kdtStaffEntities.add(kdtStaffEntity);
        }

        // saveAll()을 사용하여 여러 개의 강사 매니저 엔티티를 한 번에 저장
        if (!kdtStaffEntities.isEmpty()) {
            kdtStaffRepository.saveAll(kdtStaffEntities);
            return 1; // 강사 or 매니저 등록 성공
        }

        // 강사 등록 실패 (비어있는 경우)
        return 0; // 실패 (강사가 없거나, 다른 이유로 저장되지 않은 경우)
    }

    @Override
    public boolean findUser(String email) {
        // 이메일 중복 체크
        Optional<UserEntity> existingUser = userRepository.findByUserEmail(email);

        // 이메일이 존재하면 중복된 이메일이므로 false 반환 (중복된 이메일은 사용할 수 없다)
        if (existingUser.isPresent()) {
            return false;  // 이미 사용 중인 이메일
        }

        // 이메일이 존재하지 않으면 사용 가능한 이메일이므로 true 반환
        return true;  // 사용 가능한 이메일
    }



    // 이메일 인증번호 발송 처리
    @Override
    public boolean sendVerificationEmail(String email) {
        if (userRepository.findByUserEmail(email).isPresent()) {
            return false;  // 이미 사용 중인 이메일이라면 false 반환
        }

        // 인증번호 생성 및 이메일 전송
        String verificationCode = emailService.generateVerificationCode();
        emailService.sendVerificationEmail(email, verificationCode);
        return true;  // 인증 이메일이 전송되었으면 true 반환
    }



    // 이메일 중복 확인
    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.findByUserEmail(email).isPresent(); // 이메일 중복 체크
    }

    // 이메일 인증 코드 검증
    @Override
    public boolean verifyEmailCode(EmailVerificationDTO emailVerificationDTO) {
        String email = emailVerificationDTO.getUserEmail();
        String verificationCode = emailVerificationDTO.getVerificationCode();

        // 인증 코드 확인 로직
        if (email == null || verificationCode == null || email.trim().isEmpty() || verificationCode.trim().isEmpty()) {
            return false;  // null 체크 및 빈 문자열 체크
        }

        // 인증 코드 검증 로직 (예: DB나 캐시를 통해 확인)
        return emailService.verifyEmailCode(email, verificationCode);  // 인증 코드 확인
    }

    @Override
    public String findStaffNameByKdtStaffId(Long kdtStaffId) {
        return kdtStaffRepository.findByKdtStaffId(kdtStaffId).getUserEntity().getName();
    }

    @Override
    public String findStaffNameByKdtPartId(Long kdtPartId) {
        return kdtPartRepository.findByKdtPartId(kdtPartId).getUserEntity().getName();
    }

//    @Override
//    public Page<UserSignUpDTO> findStudentAll(String searchName, Pageable pageable) {
//        Page<UserEntity> students;
//
//        if (searchName == null || searchName.isEmpty()) {
//            // 검색어가 없으면 전체 조회
//            students = userRepository.findByUserRole("STUDENT", pageable);
//        } else {
//            // 검색어가 있으면 이름에 대해 LIKE 검색
//            students = userRepository.findByUserRoleAndNameContainingIgnoreCase("STUDENT", searchName, pageable);
//        }
//
//        // UserEntity 목록을 UserSignUpDTO로 변환하여 반환
//        return students.map(user -> new UserSignUpDTO(
//                user.getUserId(),
//                user.getUserEmail(),
//                null,  // 비밀번호는 제외
//                user.getUserRole(),
//                user.getName(),
//                user.getUserGender(),
//                user.getUserBirth(),
//                user.getUserPhone(),
//                user.getUserPostcode(),
//                user.getUserAddress(),
//                user.getUserAddressDetail(),
//                user.getUserEduLevel(),
//                user.getUserMarketingAgree(),
//                user.getUserPrivacyAgree(),
//                user.getUserStatus().getText(),
//                user.getUserCreatedAt(),
//                user.getUserUpdatedAt(),
//                user.getUserSns(),
//                user.getUserThumbnail(),
//                user.getUserLastLogin()
//        ));
//    }

    @Override
    public Page<UserSignUpDTO> findStudentAll(String searchName, Pageable pageable, Long sessionId) {
        Page<UserEntity> students;

        if (searchName == null || searchName.isEmpty()) {
            // 검색어가 없으면 전체 조회, 세션에 참여한 유저 제외
            students = userRepository.findByUserRoleAndUserIdNotIn(sessionId, pageable);
        } else {
            // 검색어가 있으면 이름에 대해 LIKE 검색, 세션에 참여한 유저 제외
            students = userRepository.findByUserRoleAndNameContainingIgnoreCaseAndUserIdNotIn(searchName, sessionId, pageable);
        }

        // UserEntity 목록을 UserSignUpDTO로 변환하여 반환
        Page<UserSignUpDTO> studentAll = students.map(user -> new UserSignUpDTO(
                user.getUserId(),
                user.getUserEmail(),
                null,  // 비밀번호는 제외
                user.getUserRole(),
                user.getName(),
                user.getUserGender(),
                user.getUserBirth(),
                user.getUserPhone(),
                user.getUserPostcode(),
                user.getUserAddress(),
                user.getUserAddressDetail(),
                user.getUserEduLevel(),
                user.getUserMarketingAgree(),
                user.getUserPrivacyAgree(),
                user.getUserStatus().getText(),
                user.getUserCreatedAt(),
                user.getUserUpdatedAt(),
                user.getUserSns(),
                user.getUserThumbnail(),
                user.getUserLastLogin()
        ));

        return studentAll;  // 학생 목록 반환
    }

    @Override
    public UserIdDTO findUserByNameBirthAndPhone(FindUserIdDTO findUserIdDTO) {
        // 이름, 생년월일, 연락처로 사용자 조회
        UserEntity userEntity = userRepository.findByNameAndUserBirthAndUserPhone(
                findUserIdDTO.getName(),
                findUserIdDTO.getUserBirth(),
                findUserIdDTO.getUserPhone()
        );

        if (userEntity != null) {
            // 사용자 정보를 찾았다면 UserIdDTO를 반환
            return new UserIdDTO(userEntity.getUserEmail());
        } else {
            // 사용자가 없다면 null 반환
            return null;
        }
    }

    //회원정보 상세보기
    @Override
    public UserSignUpDTO getUserById(Long userId) {
        // UserRepository에서 사용자 정보 조회
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        // 사용자가 없으면 RuntimeException을 던짐
        if (!userEntityOptional.isPresent()) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        // 사용자 정보가 있으면 UserSignUpDTO로 변환하여 반환
        UserEntity userEntity = userEntityOptional.get();

        return new UserSignUpDTO(
                userEntity.getUserId(),
                userEntity.getUserEmail(),
                null,  // 비밀번호는 제외
                userEntity.getUserRole(),
                userEntity.getName(),
                userEntity.getUserGender(),
                userEntity.getUserBirth(),
                userEntity.getUserPhone(),
                userEntity.getUserPostcode(),
                userEntity.getUserAddress(),
                userEntity.getUserAddressDetail(),
                userEntity.getUserEduLevel(),
                userEntity.getUserMarketingAgree(),
                userEntity.getUserPrivacyAgree(),
                userEntity.getUserStatus().getText(),
                userEntity.getUserCreatedAt(),
                userEntity.getUserUpdatedAt(),
                userEntity.getUserSns(),
                userEntity.getUserThumbnail(),
                userEntity.getUserLastLogin()
        );
    }


    @Override
    public boolean deleteUser(Long userId) {
        // 유저가 존재하는지 확인
        Optional<UserEntity> user = userRepository.findById(userId);

        if (user.isPresent()) {
            // 유저가 존재하면 삭제
            userRepository.deleteById(userId);
            return true;  // 삭제 성공
        } else {
            // 유저가 존재하지 않으면 실패
            return false;  // 삭제 실패
        }
    }

    @Override
    public int instructor(UserSignUpDTO userSignUpDTO) {
        try {
            // 이메일 중복 체크
            if (userRepository.findByUserEmail(userSignUpDTO.getUserEmail()).isPresent()) {
                return 2;  // 이미 사용 중인 이메일이라면 2 반환 (중복된 이메일)
            }

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(userSignUpDTO.getUserPw());

            // UserEntity 객체 생성 후 저장
            UserEntity userEntity = UserEntity.builder()
                    .userEmail(userSignUpDTO.getUserEmail()) // 이메일
                    .userPw(encodedPassword) // 암호화된 비밀번호
                    .userRole("INSTRUCTOR")  // 기본 역할은 "INSTRUCTOR"
                    .name(userSignUpDTO.getName()) // 이름
                    .userGender(userSignUpDTO.getUserGender()) // 성별
                    .userBirth(userSignUpDTO.getUserBirth()) // 생일
                    .userPhone(userSignUpDTO.getUserPhone()) // 전화번호
                    .userPostcode(userSignUpDTO.getUserPostcode()) // 우편번호
                    .userAddress(userSignUpDTO.getUserAddress()) // 주소
                    .userAddressDetail(userSignUpDTO.getUserAddressDetail()) // 상세주소
                    .userEduLevel(userSignUpDTO.getUserEduLevel()) // 학력
                    .userMarketingAgree(userSignUpDTO.getUserMarketingAgree()) // 마케팅 동의
                    .userPrivacyAgree(userSignUpDTO.getUserPrivacyAgree()) // 개인정보 동의
                    .userStatus(UserStatus.ACTIVE)  // 기본 상태는 "ACTIVE"
                    .build();

            // UserEntity 저장
            userRepository.save(userEntity);

            return 1;  // 회원가입 성공 시 1 반환
        } catch (Exception e) {
            // 예외 발생 시 오류 로그를 남기고 3 반환
            e.printStackTrace();  // 로깅을 대신하여 스택 트레이스를 출력
            return 3;  // 예외 발생 시 3 반환 (저장 실패)
        }
    }

    //매니저 회원정보 상세 조회 -- 관리자 제외하고 다 보여주기
    @Override
    public List<UserSignUpDTO> allAdminsExcluded() {
        // 'ADMIN' 권한을 제외한 사용자만 가져오기
        List<UserEntity> userEntities = userRepository.findByUserRoleNot("ADMIN");

        // UserEntity 리스트를 UserSignUpDTO 리스트로 변환
        List<UserSignUpDTO> userall = userEntities.stream()
                .map(user -> new UserSignUpDTO(
                        user.getUserId(),
                        user.getUserEmail(),
                        null,  // 비밀번호는 제외
                        user.getUserRole(),
                        user.getName(),
                        user.getUserGender(),
                        user.getUserBirth(),
                        user.getUserPhone(),
                        user.getUserPostcode(),
                        user.getUserAddress(),
                        user.getUserAddressDetail(),
                        user.getUserEduLevel(),
                        user.getUserMarketingAgree(),
                        user.getUserPrivacyAgree(),
                        user.getUserStatus().getText(),  // UserStatus enum을 String으로 변환
                        user.getUserCreatedAt(),
                        user.getUserUpdatedAt(),
                        user.getUserSns(),
                        user.getUserThumbnail(),
                        user.getUserLastLogin()
                ))
                .collect(Collectors.toList());

        return userall;
    }

    //매니저가 관리자인 정보를 찾을 경우 처리하는 에러
    @Override
    public UserSignUpDTO getUserByIdNoadmin(Long userId) {
        // UserRepository에서 사용자 정보 조회
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        // 사용자가 없으면 RuntimeException을 던짐
        if (!userEntityOptional.isPresent()) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        // 사용자 정보가 있으면 UserSignUpDTO로 변환하여 반환
        UserEntity userEntity = userEntityOptional.get();

        // 관리자인 경우 (role이 ADMIN이면 처리하지 않음)
        if ("ADMIN".equals(userEntity.getUserRole())) {
            return null;  // 관리자면 null 반환 (또는 다른 적절한 처리를 할 수 있음)
        }

        // 관리자가 아닌 사용자에 대해서만 UserSignUpDTO 반환
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO(
                userEntity.getUserId(),
                userEntity.getUserEmail(),
                null,  // 비밀번호는 제외
                userEntity.getUserRole(),
                userEntity.getName(),
                userEntity.getUserGender(),
                userEntity.getUserBirth(),
                userEntity.getUserPhone(),
                userEntity.getUserPostcode(),
                userEntity.getUserAddress(),
                userEntity.getUserAddressDetail(),
                userEntity.getUserEduLevel(),
                userEntity.getUserMarketingAgree(),
                userEntity.getUserPrivacyAgree(),
                userEntity.getUserStatus().getText(),
                userEntity.getUserCreatedAt(),
                userEntity.getUserUpdatedAt(),
                userEntity.getUserSns(),
                userEntity.getUserThumbnail(),
                userEntity.getUserLastLogin()
        );

        return userSignUpDTO;  // 객체를 변수에 담아 리턴
    }

    @Override
    public boolean adminUserUpdate(UserSignUpDTO userSignUpDTO) {
        if (userSignUpDTO == null || userSignUpDTO.getUserId() == null) {
            // 유효한 userSignUpDTO가 아닌 경우 처리
            return false;
        }

        // userSignUpDTO에서 받은 유저 ID로 DB에서 사용자 조회
        UserEntity user = userRepository.findById(userSignUpDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보를 찾을 수 없습니다."));

        // 유저 정보를 업데이트
        user.userUpdate(userSignUpDTO);  // 엔티티의 userUpdate 메서드를 사용하여 업데이트

        // 변경 사항을 DB에 저장
        userRepository.save(user);

        return true;  // 업데이트가 성공적으로 완료되었을 경우 true 반환
    }

    @Override
    public String getUserRole(Long userId) {
        // Optional<UserEntity> 반환하므로 Optional로 감싸서 처리
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // userRole을 그대로 반환
        return user.getUserRole();
    }


    //유저 패스워드가맞는지 확인하는 메서드
    @Override
    public boolean password(UserPassWordDTO userPassWordDTO) {
        // 1. 입력된 비밀번호(userPassWordDTO.getUserPw())가 실제 비밀번호와 일치하는지 확인
        UserEntity user = userRepository.findById(userPassWordDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 현재 비밀번호와 입력된 비밀번호 비교
        if (!passwordEncoder.matches(userPassWordDTO.getUserPw(), user.getPassword())) {
            // 현재 비밀번호가 일치하지 않으면 false 반환
            return false;
        }

        // 3. 새 비밀번호를 엔티티의 메서드를 통해 업데이트
        user.userPassWordUpdate(userPassWordDTO);

        // 4. 변경된 비밀번호 저장
        userRepository.save(user);

        return true;  // 비밀번호 변경 성공
    }




}
