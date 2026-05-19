package com.Meta_learning.manager.managercontroller;

import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardFileDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartDTO;
import com.Meta_learning.KDT.KDTDTO.KDTPartDTO.KDTPartTotalDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTStaffDTO.KDTStaffDTO;
import com.Meta_learning.KDT.KDTentity.KDTAppConsultEntity.KDTAppConsultStatus;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.KDT.KDTservice.KDTAppConsultService.KDTAppConsultService;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.Meta_learning.security.SecurityUtils.hasRole;

@Controller
@RequiredArgsConstructor
public class ManagerController {

    @Autowired
    private StaffPermissionService staffPermissionService;

    private final UserService userService;
    private final KDTService kdtService;
    private final KDTPartservice kdtPartservice;
    private final KDTAppConsultService kdtAppConsultService;
    private final BoardService boardService;

    //매니저 메인 페이지

    @GetMapping("/managers/main/mypage")
    public String instrMain(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userManager = userService.getUserById(userId);

        // 유저 정보가 있으면 마이페이지로 리턴
        if (userManager != null) {
            model.addAttribute("userManager", userManager);
            return "managers/users/mainmypage";  // 관리자 마이페이지로 이동
        } else {
            // 유저 정보가 없으면 메시지 표시 후 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/managers/mypage");  // 관리자 마이페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴

        }

    }

    //강사 등록하는 곳
    @GetMapping("/managers/KDT/{TkdtSessionId}/staff/instr")
    public String registerInstructor(@PathVariable Long TkdtSessionId, Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(TkdtSessionId)) {

                // 등록이 안된 강사 정보만 가져옴
                List<UserSignUpDTO> userinstr = userService.userinstrall(TkdtSessionId);

                // 세션 정보 가져오기
                KDTSessionDTO sessions = kdtService.getSessionsBySessId(TkdtSessionId);

                // 이미 등록된 강사 명단 가져오기
                List<UserSignUpDTO> registeredInstructors = userService.userRegisteredInstructors(TkdtSessionId);

                // 모델에 사용자 정보와 세션 정보 추가
                model.addAttribute("userinstr", userinstr);
                model.addAttribute("sessions", sessions);
                model.addAttribute("registeredInstructors", registeredInstructors);

                return "managers/KDT/staffinstr";
            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 경우
            model.addAttribute("msg", "인증되지 않은 사용자입니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 인증되지 않은 사용자 페이지로 이동
        }
    }

    // 강사 등록 처리 메서드
    @PostMapping("/managers/KDT/{sessionId}/staff/instr")
    public String registerInstructors(@PathVariable Long sessionId,
                                      @ModelAttribute KDTStaffDTO kdtStaffDTO,
                                      Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(sessionId)) {

                // 강사 등록 처리
                int result = userService.instrsave(kdtStaffDTO);

                // 결과에 따라 다르게 처리
                boolean userSave = false; // 결과를 추적할 변수

                switch (result) {
                    case 1:
                        userSave = true;  // 강사 등록 성공
                        break;
                    case 2:
                        // 다른 케이스 처리 (필요하면 로직 추가)
                        break;
                    case 0:
                        // 강사 등록 실패
                        userSave = false;
                        break;
                    default:
                        // 예외 처리
                        userSave = false;
                        break;
                }

                // 강사 배정 성공 여부에 따라 메시지와 리디렉션 URL 설정
                if (userSave) {
                    // 강사 배정 성공
                    model.addAttribute("msg", "강사배정이 완료되었습니다");
                    model.addAttribute("loc", String.format("/managers/KDT/%d/staff/instr", sessionId));  // 동적으로 sessionId를 삽입
                } else {
                    // 강사 배정 실패
                    model.addAttribute("msg", "강사배정이 실패했습니다");
                    model.addAttribute("loc", String.format("/managers/KDT/%d/staff/instr", sessionId));  // 동적으로 sessionId를 삽입
                }

                // 메시지를 보여주는 페이지로 이동
                return "utility/message";

            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 경우
            model.addAttribute("msg", "인증되지 않은 사용자입니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 인증되지 않은 사용자 페이지로 이동
        }
    }


    // 상담신청 인원 조회하는 메서드
    @GetMapping("/managers/KDT/{sessionId}/appconsult/list")
    public String getAppConsultList(@PathVariable Long sessionId,
                                    @RequestParam(defaultValue = "1") int page,  // 페이지 번호, 기본값 1
                                    @RequestParam(defaultValue = "10") int size,  // 페이지 크기, 기본값 10
                                    @RequestParam(defaultValue = "") String searchName,  // 검색어
                                    @RequestParam(required = false) String status, // status는 필수 입력이 아닌 선택 필드
                                    Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(sessionId)) {

                // 페이지 번호는 0부터 시작하므로 -1 해줍니다.
                Pageable pageable = PageRequest.of(page - 1, size);

                // status 문자열을 KDTAppConsultStatus enum으로 변환
                KDTAppConsultStatus consultStatus = null;
                if (status != null && !status.isEmpty()) {
                    try {
                        consultStatus = KDTAppConsultStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // 잘못된 status가 들어왔을 경우 예외 처리
                        consultStatus = null;  // 예: 상태값을 null로 설정하거나 예외 처리
                    }
                }

                // 페이징과 검색을 포함한 서비스 호출
                Page<KDTAppConsultDTO> appConsultPage = kdtService.getAppConsultAllview(searchName, sessionId, consultStatus, pageable);

                // 페이징 블록 관련 로직
                int pagingBlock = 5;
                int currentPage = appConsultPage.getNumber() + 1;  // 1-based 페이지 번호로 변경
                int totalPages = appConsultPage.getTotalPages();
                long totalElements = appConsultPage.getTotalElements();

                int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
                int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

                boolean noResults = totalElements == 0;

                // 모델에 데이터를 추가
                model.addAttribute("appConsultPage", appConsultPage.getContent());
                model.addAttribute("currentPage", currentPage);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("pagingBlock", pagingBlock);
                model.addAttribute("blockStart", blockStart);
                model.addAttribute("blockEnd", blockEnd);

                boolean hasPreviousBlock = currentPage > pagingBlock;
                boolean hasNextBlock = blockEnd < totalPages;

                model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
                model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우

                model.addAttribute("searchName", searchName);
                model.addAttribute("noResults", noResults);
                model.addAttribute("totalElements", totalElements);
                model.addAttribute("status", consultStatus);  // 상태 파라미터를 모델에 전달

                KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);
                model.addAttribute("sessionsDetail", sessionsDetail);

                KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

                // 현재 회차의 수강생 수
                model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가

            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 경우
            model.addAttribute("msg", "인증되지 않은 사용자입니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 인증되지 않은 사용자 페이지로 이동
        }

        return "managers/KDT/appconsultlist";  // 페이지 경로 반환
    }


    // 상담신청한 인원들 상세페이지 확인하는 메서드
    @GetMapping("/managers/KDT/{sessionId}/appconsult/{consultId}")
    public String viewConsultDetail(@PathVariable Long sessionId,
                                    @PathVariable Long consultId,
                                    Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 매니저인지 확인하는 로직
            if (staffPermissionService.hasAccessToSession(sessionId)) {

                // 상담 상세 정보를 가져오는 서비스 호출
                KDTAppConsultDTO viewConsultDetail = kdtAppConsultService.appConsultSave(sessionId, consultId);

                // 모델에 viewConsultDetail 추가
                model.addAttribute("viewConsultDetail", viewConsultDetail);

                // 상세 페이지로 이동 (이 페이지는 appconsultdetail.html)
                return "managers/KDT/appconsultdetail";

            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 경우
            model.addAttribute("msg", "인증되지 않은 사용자입니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 인증되지 않은 사용자 페이지로 이동
        }
    }


    //최고의 코드
    //상담신청인원 수정하는 메서드 ===== 최적의 코드
    @GetMapping("/managers/KDT/{sessionId}/appconsult/edit/{consultId}")
    public String getEditConsult(@PathVariable Long sessionId,
                                 @PathVariable Long consultId,
                                 Model model) {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        // 권한이 있을 경우 수정할 상담 정보 가져오기
        KDTAppConsultDTO viewConsultDetail = kdtAppConsultService.appConsultSave(sessionId, consultId);

        // 상세 페이지에서 조회된 상담 정보를 뷰에 전달
        model.addAttribute("viewConsultDetail", viewConsultDetail);

        return "managers/KDT/appconsultedit";  // 수정 페이지
    }


    // 상담 수정 페이지 수정
    @PostMapping("/managers/KDT/{sessionId}/appconsult/edit/{consultId}")
    public String editConsult(@PathVariable Long sessionId, @PathVariable Long consultId,
                              @ModelAttribute KDTAppConsultDTO kdtAppConsultDTO, Model model) {
        // 서비스 메서드 호출, DTO도 함께 전달
        boolean updateSuccess = kdtAppConsultService.appConsultFindById(sessionId, consultId, kdtAppConsultDTO);

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 수정이 성공했으면
            if (updateSuccess) {
                model.addAttribute("msg", "수정이 완료되었습니다.");
                model.addAttribute("loc", String.format("/managers/KDT/%d/appconsult/list", sessionId));  // sessionId 동적으로 삽입
            } else {
                // 수정 실패 시
                model.addAttribute("msg", "수정이 실패했습니다.");
                model.addAttribute("loc", String.format("/managers/KDT/%d/appconsult/list", sessionId));  // sessionId 동적으로 삽입
            }

            // 메시지를 보여주는 페이지로 이동
            return "utility/message";
        } else {
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }
    }


    //회차별 수강생 등록하기
    @GetMapping("/managers/KDT/{sessionId}/part")
    public String showStudentList(@PathVariable Long sessionId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,  // 기본값 설정
                                  @RequestParam(defaultValue = "") String searchName,
                                  Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 인증된 사용자 이름

            // 관리자 또는 매니저 권한 확인
            if (hasRole("ROLE_ADMIN", principal) || staffPermissionService.hasAccessToSession(sessionId)) {

                // 페이지 번호는 0부터 시작하므로 -1 해줍니다.
                Pageable pageable = PageRequest.of(page - 1, size);

                // 전체 학생 리스트를 가져옴
                Page<UserSignUpDTO> studentAll = userService.findStudentAll(searchName, pageable, sessionId);

                // 페이징 블록 관련 로직
                int pagingBlock = 5;
                int currentPage = studentAll.getNumber() + 1;  // 1-based 페이지 번호로 변경
                int totalPages = studentAll.getTotalPages();
                long totalElements = studentAll.getTotalElements();

                // 페이징 블록 시작과 끝 페이지 계산
                int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
                int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

                // 검색 결과가 없을 경우
                boolean noResults = totalElements == 0;

                // 모델에 데이터 추가
                model.addAttribute("studentAll", studentAll.getContent());
                model.addAttribute("currentPage", currentPage);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("pagingBlock", pagingBlock);
                model.addAttribute("blockStart", blockStart);
                model.addAttribute("blockEnd", blockEnd);

                boolean hasPreviousBlock = currentPage > pagingBlock;
                boolean hasNextBlock = blockEnd < totalPages;

                model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
                model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우

                model.addAttribute("searchName", searchName);
                model.addAttribute("noResults", noResults);
                model.addAttribute("totalElements", totalElements);

                // 세션 상세 정보 및 수강생 수 가져오기
                KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);
                KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

                // 세션 정보와 수강생 수를 모델에 추가
                model.addAttribute("sessionsDetail", sessionsDetail);
                model.addAttribute("sessionId", sessionId);
                model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가

                return "managers/KDT/part"; // 학생 등록 페이지의 뷰 이름
            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 사용자일 경우 (로그인되지 않은 경우)
            model.addAttribute("msg", "로그인 후 접근이 가능합니다.");
            model.addAttribute("loc", "/login");  // 로그인 페이지로 리다이렉트
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }
    }

    //학생들 회차별 신청하기
    @PostMapping("/managers/KDT/{sessionId}/part")
    public String registerStudents(@PathVariable Long sessionId,
                                   KDTPartDTO kdtPartDTO, Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 있을 때만 진행
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // 이메일 가져오기

            // 인증된 사용자가 "ROLE_MANAGER" 권한을 가지고 있는지 확인
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))) {

                // 세션에 학생 등록 시도
                int studentsSave = kdtPartservice.studentSessionsSave(kdtPartDTO);

                // 등록 성공
                if (studentsSave == 1) {
                    model.addAttribute("msg", "학생이 성공적으로 등록되었습니다.");
                    model.addAttribute("loc", String.format("/managers/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
                }
                // 인원이 꽉 찼을 경우
                else if (studentsSave == 2) {
                    model.addAttribute("msg", "등록 가능한 인원이 초과되었습니다. 인원이 꽉 찼습니다.");
                    model.addAttribute("loc", String.format("/managers/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
                }
                // 실패 시
                else if (studentsSave == 3) {
                    model.addAttribute("msg", "등록에 실패했습니다. 다시 시도해 주세요.");
                    model.addAttribute("loc", String.format("/managers/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
                }

                // 메시지를 보여주는 페이지로 이동
                return "utility/message";
            } else {
                // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
                model.addAttribute("msg", "매니저 권한이 필요합니다.");
                model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
                return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
            }
        } else {
            // 인증되지 않은 사용자일 경우 (로그인되지 않은 경우)
            model.addAttribute("msg", "로그인 후 접근이 가능합니다.");
            model.addAttribute("loc", "/login");  // 로그인 페이지로 리다이렉트
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }
    }

    //관리자는 무조건 다 접근 가능함
    @GetMapping("/managers/KDT/{sessionId}/part/list")
    public String getCsvUploadPage(@PathVariable("sessionId") Long sessionId, Model model) {

        // 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증되지 않은 경우 바로 처리
        if (!(principal instanceof UserDetails)) {
            model.addAttribute("msg", "인증되지 않은 사용자입니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 인증되지 않은 사용자 페이지로 이동
        }

        // 인증된 사용자 정보 가져오기
        String username = ((UserDetails) principal).getUsername();

        // 관리자가 아니고, 매니저 권한이 없을 경우
        if (!hasRole("ROLE_ADMIN", principal) && !staffPermissionService.hasAccessToSession(sessionId)) {
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 세션 상세 정보와 수강생 수 가져오기 (조건에 맞으면 한 번만 실행)
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);
        KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

        // 모델에 추가
        model.addAttribute("sessionsDetail", sessionsDetail);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가

        // 수정 페이지로 이동
        return "managers/KDT/partlist";
    }


    //매니저 마이 페이지
    @GetMapping("/managers/mypage")
    public String managerMypage(@AuthenticationPrincipal UserEntity user, Model model){

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userManager= userService.getUserById(userId);

        // 유저 정보가 있으면 마이페이지로 리턴
        if (userManager != null) {
            model.addAttribute("userManager", userManager);
            return "managers/users/mydetail";  // 관리자 마이페이지로 이동
        } else {
            // 유저 정보가 없으면 메시지 표시 후 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/managers/mypage");  // 관리자 마이페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }


    }








    // 특정 사용자의 상세 정보를 가져오는 GET 요청 처리
    @GetMapping("/managers/users/{userId}")
    public String getUserById(@PathVariable Long userId, Model model) {
        // 사용자 ID로 정보를 가져오는 서비스 호출
        UserSignUpDTO user = userService.getUserByIdNoadmin(userId);

        // 사용자 정보가 있을 경우
        if (user != null) {
            model.addAttribute("user", user);
            return "managers/users/userdetail";  // userDetail.html 뷰 반환
        }

        // 사용자 정보가 없거나 권한이 없을 경우
        model.addAttribute("msg", "권한이 없습니다.");
        model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
        return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
    }

    //회차 담당자 보는 곳
    @GetMapping("/managers/KDT/{sid}/staff/list")
    public String getStaffList(@PathVariable("sid") Long sid, Model model) {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sid)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 이미 등록된 강사 명단 가져오기
        List<UserSignUpDTO> registeredInstructors = userService.userRegisteredInstructors(sid);
        // 이미 등록된 매니저 명단 가져오기
        List<UserSignUpDTO> registeredManagers = userService.userRegisteredManager(sid);

        // 회차 정보 가져오기
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sid);

        // 모델에 데이터 추가
        model.addAttribute("registeredInstructors", registeredInstructors);
        model.addAttribute("registeredManagers", registeredManagers);
        model.addAttribute("sessionsDetail", sessionsDetail); // 회차 정보 추가

        // 뷰 반환
        return "managers/KDT/stafflist"; // 반환할 뷰 이름
    }


    //메니저가 회차 수정하기
    @GetMapping("/managers/KDT/session/update/{SessionId}")
    public String updateSession(@PathVariable Long SessionId, Model model) {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(SessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        // session.kdtSessionId 값을 사용하여 해당 회차 정보를 가져옵니다.
        KDTSessionDTO sessionDTO = kdtService.getSessionsBySessId(SessionId);

        // Model에 데이터를 추가하여 뷰로 전달
        model.addAttribute("sessionDTO", sessionDTO);

        return "managers/KDT/sessionupdate";
    }


    //회차 수정하는 메서드임
    @PostMapping("/managers/KDT/session/update/{id}")
    public String updateSession(@PathVariable Long id, // 변수명 수정: SessionId -> id
                                KDTSessionDTO kdtSessionDto,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam("existingThumbnail") String existingThumbnail,
                                Model model) throws IOException {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(id)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 기본적으로 기존 썸네일 사용
        String thumbnailFileName = existingThumbnail;
        if(existingThumbnail==null || existingThumbnail.isEmpty()){
            thumbnailFileName =null;
        }

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images/course";
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    if (deleted) {
                    } else {
                    }
                }
            }

            // 2. 새 파일 업로드 처리
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images/course";
            Path uploadPath = Paths.get(uploadDir);

            // 디렉토리가 존재하지 않으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 새 파일 업로드 처리
            String originalFileName = file.getOriginalFilename();
            String uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = uploadPath.resolve(uuidFileName);

            // 파일을 저장하고, UUID 파일명을 썸네일로 설정
            Files.write(filePath, file.getBytes());
            thumbnailFileName = uuidFileName; // 새 파일명으로 설정
        }

        // 수정된 썸네일 파일명을 kdtSessionDto에 저장
        kdtSessionDto.setKdtSessionThumbnail(thumbnailFileName);

        try {
            // 세션 업데이트 처리
            Boolean updateSession = kdtService.updateSession(id, kdtSessionDto); // 변수명 수정: sessionId -> id, sessionDTO -> kdtSessionDto

            if (updateSession) {
                // 수정 성공 시
                model.addAttribute("msg", "국비과정 회차가 성공적으로 수정되었습니다.");
                model.addAttribute("loc", String.format("/view/manager/KDT/session/%d", id));  // 수정된 id를 URL에 반영
            } else {
                // 수정 실패 시
                model.addAttribute("msg", "국비과정 회차 수정이 실패했습니다. 다시 시도해주세요.");
                model.addAttribute("loc", "/managers/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
            }
        } catch (EntityNotFoundException e) {
            // 세션을 찾을 수 없는 경우
            model.addAttribute("msg", "해당 세션을 찾을 수 없습니다.");
            model.addAttribute("loc", String.format("/view/manager/KDT/course/%d", id));  // 세션 목록 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            // 잘못된 상태 값 등이 전달된 경우
            model.addAttribute("msg", "잘못된 상태 값이 전달되었습니다.");
            model.addAttribute("loc", "/managers/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
        } catch (Exception e) {
            // 기타 예외 처리
            model.addAttribute("msg", "세션 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
            model.addAttribute("loc", "/managers/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
        }

        return "utility/message";  // 메시지를 보여주는 페이지
    }







    //매니저 마이페이지 수정하는 곳
    @GetMapping("/managers/mypage/update")
    public String getManagerMypageUpdate(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        UserSignUpDTO userManager = userService.getUserById(userId);

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (userManager != null) {
            model.addAttribute("userManager", userManager);  // 올바르게 괄호를 닫음
            return "managers/users/mypageupdate";  // userDetail.html 뷰 반환
        } else {
            // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "managers/mypage");  // 잘못된 문자열을 수정
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }

    }


    //매니저가 자기 계정 수정했을 때
    @PostMapping("/managers/mypage/update")
    public String postManagerUserUpdate(
            Model model,
            UserSignUpDTO userSignUpDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("existingThumbnail") String existingThumbnail, @AuthenticationPrincipal UserEntity user) {

        Long userId = user.getUserId();

        // 기존 썸네일 파일명을 기본으로 설정
        String thumbnailFileName = existingThumbnail;

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {  // 파일이 존재할 때만 처리
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/manager"; // 서버 경로 확인 필요
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    // 로그는 삭제되었으므로, 더 이상 로그를 남기지 않음
                }
            }

            // 2. 새 파일 업로드 처리
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/manager"; // 서버 경로 확인 필요
            Path uploadPath = Paths.get(uploadDir);

            // 디렉토리가 존재하지 않으면 생성
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 새 파일 업로드 처리
                String originalFileName = file.getOriginalFilename();
                String uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                // 파일을 저장하고, UUID 파일명을 썸네일로 설정
                Files.write(filePath, file.getBytes());
                thumbnailFileName = uuidFileName; // 새 파일명으로 설정

            } catch (IOException e) {
                model.addAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                model.addAttribute("loc", "/managers/mypage"); // 수정 페이지로 리다이렉트
                return "utility/message";  // 메시지 페이지로 리턴
            }
        }

        // userSignUpDTO에 썸네일 파일명과 userId 설정
        userSignUpDTO.setUserThumbnail(thumbnailFileName);
        userSignUpDTO.setUserId(userId);  // userId는 Long 그대로 사용

        // 유저 업데이트 서비스 호출
        boolean userUpdate = userService.adminUserUpdate(userSignUpDTO);

        // 유저 정보가 성공적으로 업데이트 되면
        if (userUpdate) {
            model.addAttribute("msg", "매니저 개인정보가 수정되었습니다.");
            model.addAttribute("loc", "/managers/mypage");  // 수정된 후 이동할 페이지
            return "utility/message";  // 메시지 페이지로 리턴
        } else {
            // 유저 정보 수정 실패 시
            model.addAttribute("msg", "매니저 개인정보 수정에 실패했습니다.");
            model.addAttribute("loc", "/managers/mypage");  // 수정 페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }


    //매니저 비밀번호 변경하는 곳
    @GetMapping("/managers/pw/change")
    public String getPwChangeck(){
        return "managers/users/pwchange";
    }


    //매니저 비밀번호 비밀번호 변경 받는 곳
    @PostMapping("/managers/pw/change")
    public String postPwChangeck(UserPassWordDTO userPassWordDTO, @AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();
        userPassWordDTO.setUserId(userId);

        // 비밀번호 변경 검증
        boolean isPasswordChanged = userService.password(userPassWordDTO);

        if (!isPasswordChanged) {
            // 비밀번호가 틀린 경우
            model.addAttribute("msg", "현재 비밀번호가 틀렸습니다.");
            return "managers/users/pwchange";  // 비밀번호 변경 화면으로 돌아감
        }

        // 비밀번호 변경이 성공한 경우
        model.addAttribute("msg", "비밀번호가 변경되었습니다.");
        model.addAttribute("loc", "/managers/mypage");  // 관리자 마이페이지로 리디렉션
        return "utility/message";  // 메시지 페이지로 리턴
    }

//    //매니저 자료실 게시판 보는 곳임 - 다른 카테고리는 못봄
    @GetMapping("/managers/KDT/{sid}/board/materiallist")
    public String getMaterialList(
    @PathVariable Long sid,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "") String searchValue,
    @RequestParam(defaultValue = "title") String searchType, // 검색 옵션을 추가로 받기
    Model model) {

        Long sessionid = sid; // URL에서 받은 sessionId 설정

        // Pageable 설정
        Pageable pageable = PageRequest.of(page - 1, size);

        // 서비스 메서드 호출 (searchType을 추가로 전달)
        Page<KDTBoardTitleDTO> kdtBoardMaterialPage = boardService.getPagedBoardmaterial(sessionid, searchValue, searchType, pageable);

        // 페이징 처리 및 모델 추가
        int pagingBlock = 5;
        int currentPage = kdtBoardMaterialPage.getNumber() + 1;
        int totalPages = kdtBoardMaterialPage.getTotalPages();
        long totalElements = kdtBoardMaterialPage.getTotalElements();

        int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
        int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

        boolean noResults = totalElements == 0;
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

        // 모델에 데이터 추가
        model.addAttribute("boardviewList", kdtBoardMaterialPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pagingBlock", pagingBlock);
        model.addAttribute("blockStart", blockStart);
        model.addAttribute("blockEnd", blockEnd);

        boolean hasPreviousBlock = currentPage > pagingBlock;
        boolean hasNextBlock = blockEnd < totalPages;

        model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
        model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우

        model.addAttribute("noResults", noResults);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("sessionsDetail", sessionsDetail);

        return "managers/KDT/board/kdtmateriallist";  // 뷰 이름 반환
    }


    //매니저 대시보드  작성하는 곳
    @GetMapping("/managers/KDT/{sid}/board")
    public String getManagerBoard(@PathVariable Long sid, Model model) {
        // 세션 ID를 PathVariable로 받아옴
        Long sessionid = sid;  // URL에서 전달된 sid 값을 사용

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sid)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        // 세션 정보를 가져오기 위해 서비스 호출
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

        // 카테고리 정보와 세션 정보를 모델에 추가
        model.addAttribute("categories", KDTBoardCategory.values());
        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보 추가

        // 뷰 반환
        return "managers/KDT/board/kdtboardwriting";  // "kdtboard" 뷰를 반환
    }

    // kdt 게시판 자료 보내서  넣는 곳
    @PostMapping("/managers/KDT/{sid}/board")
    public String postEmployreviewWrite(@PathVariable("sid") Long sessionId,  // sid를 @PathVariable로 받아옴
                                        @AuthenticationPrincipal UserEntity user,
                                        @ModelAttribute KDTBoardDTO kdtboardDTO,
                                        @ModelAttribute KDTBoardFileDTO kdtboardFileDTO,
                                        @RequestParam("file") MultipartFile[] files,
                                        Model model) {


        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 로그인한 사용자 정보에서 userId를 가져옵니다.
        Long userId = user.getUserId();
        kdtboardDTO.setUserId(userId);
        // BoardDTO에 userId를 설정합니다.
        kdtboardDTO.setUserId(userId);
        // 세션 ID를 URL에서 받은 sessionId로 설정
        kdtboardDTO.setKdtSessionId(sessionId);  // sessionId로 세션 ID 설정

        // 파일 목록을 담을 리스트
        List<KDTBoardFileDTO> fileList = new ArrayList<>();

        // 파일 정보 처리: KDTBoardFileDTO에 파일 정보 설정
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    KDTBoardFileDTO fileDTO = new KDTBoardFileDTO();  // KDTBoardFileDTO 사용
                    fileDTO.setKdtFileName(file.getOriginalFilename());
                    fileDTO.setKdtFileUUID(UUID.randomUUID().toString()); // UUID 생성
                    fileDTO.setKdtFileSize(file.getSize());
                    fileDTO.setKdtFileType(file.getContentType());

                    // 파일을 리스트에 추가
                    fileList.add(fileDTO);
                }
            }
        }
        // 서비스를 호출하여 게시글 작성 (여기에서 boolean 값을 반환받음)
        boolean isSaved = boardService.kdtboardsave(kdtboardDTO, fileList, files); // fileList를 BoardService에 전달

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (isSaved) {
            model.addAttribute("msg", "강의 자료가 업로드 되었습니다.");
            model.addAttribute("loc", "/managers/KDT/" + sessionId + "/board/materiallist");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // userDetail.html 뷰 반환
        } else {
            // 게시글 저장 실패 시 처리
            model.addAttribute("msg", "강의 자료 업로드에 실패했습니다.");
            model.addAttribute("loc", "/managers/KDT/" + sessionId + "/board");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }
    }


    //자료실 상세보기하는 곳
    // 예시: 컨트롤러에서 서비스 호출
    @GetMapping("/managers/KDT/{sessionId}/board/{boardId}")
    public String getBoardDetail(@PathVariable Long sessionId,
                                 @PathVariable Long boardId,
                                 Model model) {

        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }
        boardService.reviewcount(boardId);
        KDTBoardviewDTO boardDetails = boardService.getBoardDetails(sessionId, boardId, KDTBoardCategory.MATERIAL);
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);


        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보
        model.addAttribute("boardDetails", boardDetails);
        return "managers/KDT/board/kdtmaterialdetail";
    }




}
