package com.Meta_learning.admin.admincontroller;

import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardFileDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTDTO.KDTCourseDTO.KDTCourseDTO;
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
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final KDTService kdtService;
    private final UserService userService;
    private final KDTAppConsultService kdtAppConsultService;
    private final KDTPartservice kdtPartservice;
    private final BoardService boardService;

    //국비과정 등록하기
    @GetMapping("/admin/KDT/course")
    public String getkdtcourse() {
        return "admin/KDT/course";
    }

    //국비과정 등록하기
    @PostMapping("/admin/KDT/course")
    public String postkdtcourse(KDTCourseDTO kdtCourseDTO, Model model) {


        int result = kdtService.kdtcoursesave(kdtCourseDTO);

        switch (result) {
            case 1: // 성공
                model.addAttribute("msg", "국비과정이 등록이 완료되었습니다!");
                model.addAttribute("loc", "/admin/KDT/session");  // 회차 입력하는 곳으로 이동
                break;
            case 2: // 이미 있음
                model.addAttribute("msg", "이미 존재하는 국비과정입니다!");
                model.addAttribute("loc", "/admin/KDT/course");  // 다시 입력 페이지로
                break;
            default: // 실패
                model.addAttribute("msg", "국비과정 등록을 실패했습니다!");
                model.addAttribute("loc", "/admin/KDT/course");  // 다시 입력 페이지로
                break;
        }

        return "utility/message";
    }


    // 국비과정 수정하기
    @GetMapping("/admin/KDT/course/update/{courseId}")
    public String getkdtcourseupdate(@PathVariable Long courseId, Model model) {
        // Service에서 과정 정보 가져오기
        KDTCourseDTO kdtcourse = kdtService.kdtcourseupdate(courseId);

        // 모델에 DTO 객체를 담아서 뷰로 전달
        model.addAttribute("kdtcourse", kdtcourse);

        return "admin/KDT/courseupdate"; // courseupdate.html로 이동
    }


    @PostMapping("/admin/KDT/course/update/{courseId}")
    public String updateCourse(@PathVariable Long courseId, @ModelAttribute KDTCourseDTO kdtcourse, Model model) {
        // 수정된 정보를 서비스로 전달하여 업데이트 처리
        boolean update = kdtService.updateCourse(courseId, kdtcourse);

        if (update) {
            // 수정 성공 시
            model.addAttribute("msg", "국비과정이 수정되었습니다.");
            model.addAttribute("loc", "/view/admin/KDT/list");  // 목록으로 리디렉션
        } else {
            // 수정 실패 시
            model.addAttribute("msg", "국비과정 수정이 실패했습니다.");
            model.addAttribute("loc", "/admin/KDT/course/update/" + courseId);  // 수정 페이지로 리디렉션
        }

        // 결과 메시지 페이지로 이동
        return "utility/message";
    }


    //회차등록하기
    @GetMapping("/admin/KDT/session")
    public String getkdtsession(Model model) {
        List<KDTCourseDTO> courseall = kdtService.courseall();
        model.addAttribute("courseall", courseall);  // 데이터를 모델에 추가하여 뷰로 전달
        return "admin/KDT/session";  // 해당 뷰로 이동

    }

    //회차 처음을 올리는거임
    @PostMapping("/admin/KDT/session")
    public String postKdtSession(KDTSessionDTO kdtSessionDto, @RequestParam("files") MultipartFile file, Model model) throws IOException {

        // 파일 저장 경로 설정
        //String uploadDir = "src/main/resources/static/images/course"; // 업로드 디렉토리
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images/course"; // 업로드 디렉토리
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String uuidFileName = null;

        // 업로드된 파일 처리
        if(file != null && !file.isEmpty()) {
            String originalFileName = file.getOriginalFilename(); // 원본 파일 이름
            uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName; // UUID를 추가한 파일 이름
            Path filePath = uploadPath.resolve(uuidFileName); // 파일 저장 경로

        // 파일 저장
        Files.write(filePath, file.getBytes());
        }

        // UUID 파일명을 kdtSessionThumbnail에 저장
        kdtSessionDto.setKdtSessionThumbnail(uuidFileName); // UUID 파일명만 설정 (실제 경로는 저장하지 않음)

        // 회차 저장 서비스 호출
        int result = kdtService.kdtsessionsave(kdtSessionDto);

        // 결과에 따라 메시지 처리
        switch (result) {
            case 1: // 성공
                model.addAttribute("msg", "국비과정 회차 등록이 완료되었습니다!");
                model.addAttribute("loc", "/view/admin/KDT/list");  // 홈으로 리디렉션
                break;
            case 2: // 이미 존재
                model.addAttribute("msg", "이미 존재하는 회차 과정입니다!");
                model.addAttribute("loc", "/admin/KDT/session");  // 다시 입력 페이지로
                break;
            default: // 실패
                model.addAttribute("msg", "국비과정 회차 등록을 실패했습니다!");
                model.addAttribute("loc", "/admin/KDT/session");  // 다시 입력 페이지로
                break;
        }

        return "utility/message";
    }


    //회차 수정하기
    @GetMapping("/admin/KDT/session/update/{SessionId}")
    public String updateSession(@PathVariable Long SessionId, Model model) {
        // session.kdtSessionId 값을 사용하여 해당 회차 정보를 가져옵니다.
        KDTSessionDTO sessionDTO = kdtService.getSessionsBySessId(SessionId);

        // Model에 데이터를 추가하여 뷰로 전달
        model.addAttribute("sessionDTO", sessionDTO);

        return "admin/KDT/sessionupdate";
    }


    //회차 수정하는 메서드임
    @PostMapping("/admin/KDT/session/update/{id}")
    public String updateSession(@PathVariable Long id, // 변수명 수정: SessionId -> id
                                KDTSessionDTO kdtSessionDto,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam("existingThumbnail") String existingThumbnail,
                                Model model) throws IOException {

        // 기본적으로 기존 썸네일 사용
        String thumbnailFileName = existingThumbnail;
        if(existingThumbnail==null || existingThumbnail.isEmpty()){
            thumbnailFileName =null;
        }

        //세션 아이디로 코스 아이디찾기
        Long CourseId = kdtService.findCourseIdBySessionId(id);

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                //String uploadDir = "src/main/resources/static/images/course";
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
            //String uploadDir = "src/main/resources/static/images/course";
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
                model.addAttribute("loc", String.format("/view/admin/KDT/course/%d", CourseId));  // 수정된 id를 URL에 반영
            } else {
                // 수정 실패 시
                model.addAttribute("msg", "국비과정 회차 수정이 실패했습니다. 다시 시도해주세요.");
                model.addAttribute("loc", "/admin/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
            }
        } catch (EntityNotFoundException e) {
            // 세션을 찾을 수 없는 경우
            model.addAttribute("msg", "해당 세션을 찾을 수 없습니다.");
            model.addAttribute("loc", String.format("/view/admin/KDT/course/%d", CourseId));  // 세션 목록 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            // 잘못된 상태 값 등이 전달된 경우
            model.addAttribute("msg", "잘못된 상태 값이 전달되었습니다.");
            model.addAttribute("loc", "/admin/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
        } catch (Exception e) {
            // 기타 예외 처리
            model.addAttribute("msg", "세션 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
            model.addAttribute("loc", "/admin/KDT/session/update/" + id);  // 수정 페이지로 리디렉션
        }

        return "utility/message";  // 메시지를 보여주는 페이지
    }


    // 관리자 마이페이지 가는 곳
    @GetMapping("/admin/mypage")
    public String adminmypage(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userAdmin= userService.getUserById(userId);

        // 유저 정보가 있으면 마이페이지로 리턴
        if (userAdmin != null) {
            model.addAttribute("userAdmin", userAdmin);
            return "admin/users/mydetail";  // 관리자 마이페이지로 이동
        } else {
            // 유저 정보가 없으면 메시지 표시 후 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/admin/mypage");  // 관리자 마이페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }


    //관리자 비밀번호 변경하는 곳
    @GetMapping("/admin/pw/change")
    public String getPwChangeck(){
            return "admin/users/pwchange";
    }


    //관리자 비밀번호 비밀번호 변경 받는 곳
    @PostMapping("/admin/pw/change")
    public String postPwChangeck(UserPassWordDTO userPassWordDTO, @AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();
        userPassWordDTO.setUserId(userId);

        // 비밀번호 변경 검증
        boolean isPasswordChanged = userService.password(userPassWordDTO);

        if (!isPasswordChanged) {
            // 비밀번호가 틀린 경우
            model.addAttribute("msg", "비밀번호가 틀렸습니다.");
            return "admin/users/pwchange";  // 비밀번호 변경 화면으로 돌아감
        }

        // 비밀번호 변경이 성공한 경우
        model.addAttribute("msg", "비밀번호가 변경되었습니다.");
        model.addAttribute("loc", "/admin/mypage");  // 관리자 마이페이지로 리디렉션
        return "utility/message";  // 메시지 페이지로 리턴
    }





    //회차별 매니저 등록하기
    @GetMapping("/admin/KDT/{sessionId}/staff/manager")
    public String showManagerRegistrationPage(@PathVariable Long sessionId, Model model) {

        //등록이 안댄 매니저 정보만 가져옴
        List<UserSignUpDTO> usermanager = userService.usermanagerall(sessionId);

        // 세션 정보 가져오기
        KDTSessionDTO sessions = kdtService.getSessionsBySessId(sessionId);

        //이미 등록된 매니저만 가져오는 메서드
        List<UserSignUpDTO> registeredmanagers = userService.userRegisteredManager(sessionId);

        // 모델에 사용자 정보와 세션 정보 추가
        model.addAttribute("usermanager", usermanager);
        model.addAttribute("sessions", sessions);
        model.addAttribute("registeredmanagers", registeredmanagers);


        return "admin/KDT/staffmanger"; // 매니저 등록 페이지의 뷰 이름
    }


    //강사 등록하는 메서드
    @PostMapping("/admin/KDT/{sessionId}/staff/manager")
    public String registerManager(@PathVariable Long sessionId,
                                  @ModelAttribute KDTStaffDTO kdtStaffDTO,
                                  Model model) {

        // 강사 등록 처리 (여기서 매니저 배정을 위한 로직이 수행될 수 있음)
        int result = userService.instrsave(kdtStaffDTO);

        // 강사 등록 성공 여부를 추적하는 변수
        boolean userSave = false;

        // 결과에 따라 다르게 처리
        switch (result) {
            case 1:
                // 매니저 배정 성공 로직
                userSave = true;  // 매니저 배정 성공으로 처리
                break;
            case 2:
                // 다른 조건에 따른 로직 (필요하면 추가 처리)
                break;
            case 0:
                // 매니저 배정 실패 처리
                userSave = false;
                break;
            default:
                // 예외 상황 처리
                userSave = false;
                break;
        }

        // 매니저 배정 성공 여부에 따라 메시지와 리디렉션 URL 설정
        if (userSave) {
            // 매니저 배정 성공
            model.addAttribute("msg", "매니저 배정이 완료되었습니다");
            model.addAttribute("loc", String.format("/admin/KDT/%d/staff/manager", sessionId));  // 동적으로 sessionId를 삽입
        } else {
            // 매니저 배정 실패
            model.addAttribute("msg", "매니저 배정이 실패했습니다");
            model.addAttribute("loc", String.format("/admin/KDT/%d/staff/manager", sessionId));  // 동적으로 sessionId를 삽입
        }

        // 메시지를 보여주는 페이지로 이동
        return "utility/message";
    }


    //회차별 강사 등록하기
    @GetMapping("/admin/KDT/{sessionId}/staff/instr")
    public String showinstrRegistrationPage(@PathVariable Long sessionId, Model model) {

        // 등록이 안댄 강사 정보만 가져옴
        List<UserSignUpDTO> userinstr = userService.userinstrall(sessionId);

        // 세션 정보 가져오기
        KDTSessionDTO sessions = kdtService.getSessionsBySessId(sessionId);

        //이미 등록된 강사 명단 가져오기
        List<UserSignUpDTO> registeredInstructors = userService.userRegisteredInstructors(sessionId);


        // 모델에 사용자 정보와 세션 정보 추가
        model.addAttribute("userinstr", userinstr);
        model.addAttribute("sessions", sessions);
        model.addAttribute("registeredInstructors", registeredInstructors);


        return "admin/KDT/staffinstr"; // 매니저 등록 페이지의 뷰 이름
    }

    // 강사 등록 처리 메서드
    @PostMapping("/admin/KDT/{sessionId}/staff/instr")
    public String registerInstructors(@PathVariable Long sessionId,
                                      @ModelAttribute KDTStaffDTO kdtStaffDTO,
                                      Model model) {

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
            model.addAttribute("loc", String.format("/admin/KDT/%d/staff/instr", sessionId));  // 동적으로 sessionId를 삽입
        } else {
            // 강사 배정 실패
            model.addAttribute("msg", "강사배정이 실패했습니다");
            model.addAttribute("loc", String.format("/admin/KDT/%d/staff/instr", sessionId));  // 동적으로 sessionId를 삽입
        }

        // 메시지를 보여주는 페이지로 이동
        return "utility/message";
    }


    // 상담신청 인원 조회하는 메서드
    @GetMapping("/admin/KDT/{sessionId}/appconsult/list")
    public String getAppConsultList(@PathVariable Long sessionId,
                                    @RequestParam(defaultValue = "1") int page,  // 페이지 번호, 기본값 1
                                    @RequestParam(defaultValue = "10") int size,  // 페이지 크기, 기본값 10
                                    @RequestParam(defaultValue = "") String searchName,  // 검색어
                                    @RequestParam(required = false) String status, // status는 필수 입력이 아닌 선택 필드
                                    Model model) {


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

        //현재회차의 수강새 수 보는 메서드
        KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

        // 현재 회차의 수강생 수
        model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가



        return "admin/KDT/appconsultlist";  // 페이지 경로 반환
    }

    //상담신청한 인원들 상세페이지 확인하는 메서드
    @GetMapping("/admin/KDT/{sessionId}/appconsult/{consultId}")
    public String viewConsultDetail(@PathVariable Long sessionId,
                                    @PathVariable Long consultId,
                                    Model model) {

        // 상담 상세 정보를 가져오는 서비스 호출
        KDTAppConsultDTO viewConsultDetail = kdtAppConsultService.appConsultSave(sessionId, consultId);

        // 모델에 viewConsultDetail 추가
        model.addAttribute("viewConsultDetail", viewConsultDetail);

        // 상세 페이지로 이동 (이 페이지는 appconsultdetail.html)
        return "admin/KDT/appconsultdetail";
    }


    //상담 신천한 인원들 수정하는 상세페이지
    @GetMapping("/admin/KDT/{sessionId}/appconsult/edit/{consultId}")
    public String getEditConsult(@PathVariable Long sessionId, @PathVariable Long consultId, Model model) {
        // 수정할 상담 정보 가져오기
        KDTAppConsultDTO viewConsultDetail = kdtAppConsultService.appConsultSave(sessionId, consultId);

        // 상세 페이지에서 조회된 상담 정보를 뷰에 전달
        model.addAttribute("viewConsultDetail", viewConsultDetail);

        return "admin/KDT/appconsultedit";  // 수정 페이지
    }

     // 상담 수정 페이지 조회
     @PostMapping("/admin/KDT/{sessionId}/appconsult/edit/{consultId}")
     public String editConsult(@PathVariable Long sessionId, @PathVariable Long consultId,
                               @ModelAttribute KDTAppConsultDTO kdtAppConsultDTO, Model model) {
         // 서비스 메서드 호출, DTO도 함께 전달
         boolean updateSuccess = kdtAppConsultService.appConsultFindById(sessionId, consultId, kdtAppConsultDTO);

         // 수정이 성공했으면
         if (updateSuccess) {
             model.addAttribute("msg", "수정이 완료되었습니다.");
             model.addAttribute("loc", String.format("/admin/KDT/%d/appconsult/list", sessionId));  // sessionId 동적으로 삽입
         } else {
             // 수정 실패 시
             model.addAttribute("msg", "수정이 실패했습니다.");
             model.addAttribute("loc", String.format("/admin/KDT/%d/appconsult/list", sessionId));  // sessionId 동적으로 삽입
         }

         // 메시지를 보여주는 페이지로 이동
         return "utility/message";
     }




    //회차별 수강생 등록하기
    @GetMapping("/admin/KDT/{sessionId}/part")
    public String showStudentList(@PathVariable Long sessionId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,  // 기본값 설정
                                  @RequestParam(defaultValue = "") String searchName,
                                  Model model) {

        // 페이지 번호는 0부터 시작하므로 -1 해줍니다.
        Pageable pageable = PageRequest.of(page - 1, size);

        // 전체 학생 리스트를 가져옴
        // userService.findStudentAll 메서드 호출 시 sessionId를 전달
        Page<UserSignUpDTO> studentAll = userService.findStudentAll(searchName, pageable, sessionId);

        // 페이징 블록 관련 로직 (페이징 블록 계산)
        int pagingBlock = 5;
        int currentPage = studentAll.getNumber() + 1;  // 1-based 페이지 번호로 변경
        int totalPages = studentAll.getTotalPages();
        long totalElements = studentAll.getTotalElements();

        // 페이징 블록 시작과 끝 페이지 계산
        int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
        int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

        // 검색 결과가 없을 경우
        boolean noResults = totalElements == 0;

        // 모델에 데이터를 추가
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
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);
        model.addAttribute("sessionsDetail", sessionsDetail);
        model.addAttribute("sessionId", sessionId);

        KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);


        // 현재 회차의 수강생 수
        model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가


        return "admin/KDT/part"; // 학생 등록 페이지의 뷰 이름
    }

    @PostMapping("/admin/KDT/{sessionId}/part")
    public String registerStudents(@PathVariable Long sessionId,
                                   KDTPartDTO kdtPartDTO, Model model) {

        // 세션에 학생 등록 시도
        int studentsSave = kdtPartservice.studentSessionsSave(kdtPartDTO);

        // 등록 성공
        if (studentsSave == 1) {
            model.addAttribute("msg", "학생이 성공적으로 등록되었습니다.");
            model.addAttribute("loc", String.format("/admin/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
        }
        // 인원이 꽉 찼을 경우
        else if (studentsSave == 2) {
            model.addAttribute("msg", "등록 가능한 인원이 초과되었습니다. 인원이 꽉 찼습니다.");
            model.addAttribute("loc", String.format("/admin/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
        }
        // 이미 등록된 학생이 있을 경우
        else if (studentsSave == 4) {
            model.addAttribute("msg", "이미 등록된 학생입니다.");
            model.addAttribute("loc", String.format("/admin/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
        }
        // 실패 시
        else if (studentsSave == 3) {
            model.addAttribute("msg", "등록에 실패했습니다. 다시 시도해 주세요.");
            model.addAttribute("loc", String.format("/admin/KDT/%d/part", sessionId));  // sessionId 동적으로 삽입
        }

        // 메시지를 보여주는 페이지로 이동
        return "utility/message";
    }


        // CSV 업로드를 위한 GET 요청 처리
        @GetMapping("/admin/KDT/{sessionId}/part/list")
        public String getCsvUploadPage(@PathVariable("sessionId") Long sessionId, Model model) {
            KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);
            model.addAttribute("sessionsDetail", sessionsDetail);
            model.addAttribute("sessionId", sessionId);
            KDTPartTotalDTO studentCount = kdtPartservice.studentCountAll(sessionId);

            // 현재 회차의 수강생 수
            model.addAttribute("studentCount", studentCount); // 수강생 수를 모델에 추가


            // 추가적인 데이터나 뷰를 처리할 수 있습니다.
            return "admin/KDT/partlist";  // 예시 뷰 이름 (템플릿 파일 이름)
        }


    //국비과정 계정 만들기
    @GetMapping("/admin/KDT/accountmanagement")
    public String AccountManagemen(){
        return "admin/users/accountmanagement";
    }

    //국비 강사 등록하는 곳
    @GetMapping("/admin/KDT/account/Instructor")
    public String  accountInstructor(){
     return "admin/users/accountInstructor";
    }

    //강사 등록 == 권한이 자동으로 강사로 올라감
    @PostMapping("/admin/KDT/account/Instructor")
    public String saveAccountInstructor(UserSignUpDTO userSignUpDTO, Model model) {


        int instructor = userService.instructor(userSignUpDTO);

        // 등록 성공
        if (instructor == 1) {
            model.addAttribute("msg", "강사 계정이 생성되었습니다.");
            model.addAttribute("loc", "/admin/KDT/accountmanagement");  // 성공 시 관리자 계정 관리 페이지로 이동
            return "utility/message"; // 성공 메시지 페이지로 이동
        }
        // 이메일 중복
        else if (instructor == 2) {
            model.addAttribute("msg", "이미 사용 중인 이메일입니다.");
            model.addAttribute("loc", "/admin/KDT/account/instructor");  // 다시 강사 계정 등록 페이지로 돌아가기
            return "utility/message"; // 이메일 중복 메시지 페이지로 이동
        }
        // 예외 발생 (회원가입 실패)
        else if (instructor == 3) {
            model.addAttribute("msg", "강사 계정 등록 중 오류가 발생했습니다.");
            model.addAttribute("loc", "/admin/KDT/account/instructor");  // 다시 강사 계정 등록 페이지로 돌아가기
            return "utility/message"; // 오류 메시지 페이지로 이동
        }

        return "utility/message"; // 기본 메시지 페이지 (예상치 못한 오류 처리)
    }


    //국비 회원 등록하는 곳
    @GetMapping("/admin/KDT/account/studnt")
    public String  accountStudnt(){
        return "admin/users/accountstudnt";
    }


   //회차 담당자 보는 곳
   @GetMapping("/admin/KDT/{sid}/staff/list")
   public String getStaffList(@PathVariable("sid") Long sid, Model model) {
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
       return "admin/KDT/stafflist"; // 반환할 뷰 이름
   }


   //관리자 마이페이지 수정하는 곳
   @GetMapping("/admin/mypage/update")
   public String getAdminMypageUpdate(@AuthenticationPrincipal UserEntity user, Model model) {

       Long userId = user.getUserId();

       UserSignUpDTO userAdmin = userService.getUserById(userId);

       // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
       if (userAdmin != null) {
           model.addAttribute("userAdmin", userAdmin);  // 올바르게 괄호를 닫음
           return "admin/users/mypageupdate";  // userDetail.html 뷰 반환
       } else {
           // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
           model.addAttribute("msg", "유저 정보가 없습니다.");
           model.addAttribute("loc", "admin/mypage");  // 잘못된 문자열을 수정
           return "utility/message";  // 메시지를 표시할 페이지로 리턴
       }

   }

   //관리자 자료실 게시판 보는 곳임 - 다른 카테고리는 못봄
   @GetMapping("/admin/KDT/{sid}/board/materiallist")
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

       return "admin/KDT/board/kdtmateriallist";
   }



    //어드민 대시보드  작성하는 곳
    @GetMapping("/admin/KDT/{sid}/board")
    public String getAdminBoard(@PathVariable Long sid, Model model) {
        // 세션 ID를 PathVariable로 받아옴
        Long sessionid = sid;  // URL에서 전달된 sid 값을 사용

        // 세션 정보를 가져오기 위해 서비스 호출
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

        // 카테고리 정보와 세션 정보를 모델에 추가
        model.addAttribute("categories", KDTBoardCategory.values());
        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보 추가

        // 뷰 반환
        return "admin/KDT/board/kdtboardwriting";  // "kdtboard" 뷰를 반환
    }

    // kdt 게시판 자료 보내서  넣는 곳
    @PostMapping("/admin/KDT/{sid}/board")
    public String postEmployreviewWrite(@PathVariable("sid") Long sessionId,  // sid를 @PathVariable로 받아옴
                                        @AuthenticationPrincipal UserEntity user,
                                        @ModelAttribute KDTBoardDTO kdtboardDTO,
                                        @ModelAttribute KDTBoardFileDTO kdtboardFileDTO,
                                        @RequestParam("file") MultipartFile[] files,
                                        Model model) {
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
            model.addAttribute("loc", "/admin/KDT/" + sessionId + "/board/materiallist");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // userDetail.html 뷰 반환
        } else {
            // 게시글 저장 실패 시 처리
            model.addAttribute("msg", "강의 자료 업로드에 실패했습니다.");
            model.addAttribute("loc", "/admin/KDT/" + sessionId + "/board");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }
    }


    //자료실 상세보기하는 곳
    // 예시: 컨트롤러에서 서비스 호출
    @GetMapping("/admin/KDT/{sessionId}/board/{boardId}")
    public String getBoardDetail(@PathVariable Long sessionId,
                                 @PathVariable Long boardId,
                                 Model model) {
        KDTBoardviewDTO boardDetails = boardService.getBoardDetails(sessionId, boardId, KDTBoardCategory.MATERIAL);

        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);

        boardService.reviewcount(boardId);

        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보
        model.addAttribute("boardDetails", boardDetails);
        return "admin/KDT/board/kdtmaterialdetail";
    }

    @GetMapping("/admin/instr/list")
    public String getInstrList(@AuthenticationPrincipal UserEntity user,
                       Model model) {
        return "admin/pendinginstr"; // 뷰 반환
    }

}