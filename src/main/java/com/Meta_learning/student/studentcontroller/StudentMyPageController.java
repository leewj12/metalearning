package com.Meta_learning.student.studentcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionEvalDTO.KDTSessionEvalDTO;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.student.studentpermissionservice.StudentService;
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StudentMyPageController {

    private final UserService userService;
    private final StudentService studentService;
    private final StudentPermissionService studentPermissionService;
    private final KDTService kdtService;
    private final BoardService boardService;


    //회차에 등록되지 않으면 회차가 보이지 않음
//    @GetMapping("/student/main/mypage")
//    public String studentMainMypage(@AuthenticationPrincipal UserEntity user, Model model) {
//        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
//        // 유저 ID로 세션 목록을 가져옴
//        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기
//
//        // 첫 번째 세션의 ID를 가져오기
//        Long sessionId = null;
//        if (!sessions.isEmpty()) {
//            sessionId = sessions.get(0).getKdtSessionId();
//        }
//
//        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
//
//        // sessions가 비어있지 않으면 해당 정보를 모델에 추가
//        model.addAttribute("sessions", sessions);  // 세션 정보를 모델에 추가
//        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정
//
//        return "/student/users/mainmypage";
//    }


    //학생 마이페이지
    @GetMapping("/student/mypage")
    public String studentMypage(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userStudent = userService.getUserById(userId);

        // 유저 정보가 없으면 메시지 표시 후 리다이렉트
        if (userStudent == null) {
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/student/mypage");
            return "utility/message";  // 메시지 페이지로 리턴
        }

        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // 첫 번째 세션의 ID를 가져오기 (세션이 있을 경우)
        Long sessionId = sessions.isEmpty() ? null : sessions.get(0).getKdtSessionId();

        // 모델에 필요한 속성 추가
        model.addAttribute("sessionId", sessionId);  // 첫 번째 세션 ID
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true

        model.addAttribute("userStudent", userStudent);  // 유저 정보

        // 마이페이지로 이동
        return "student/users/mydetail";
    }



    //학생 마이페이지 수정하는 곳
    @GetMapping("/student/mypage/update")
    public String getAdminMypageUpdate(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        UserSignUpDTO userStudent = userService.getUserById(userId);

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (userStudent!= null) {
            model.addAttribute("userStudent", userStudent);  // 올바르게 괄호를 닫음
            return "student/users/mypageupdate";  // userDetail.html 뷰 반환
        } else {
            // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/student/mypage");  // 잘못된 문자열을 수정
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }

    }

    //학생 마이페이지 수정
    @PostMapping("/student/mypage/update")
    public String postAdminUserUpdate(
            @AuthenticationPrincipal UserEntity user,  // userId를 Long 타입으로 받기
            Model model,
            UserSignUpDTO userSignUpDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("existingThumbnail") String existingThumbnail) {

        // 기존 썸네일 파일명을 기본으로 설정
        String thumbnailFileName = existingThumbnail;

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {  // 파일이 존재할 때만 처리
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/student"; // 서버 경로 확인 필요
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    // 로그는 삭제되었으므로, 더 이상 로그를 남기지 않음
                }
            }

            // 2. 새 파일 업로드 처리
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/student"; // 서버 경로 확인 필요
            Path uploadPath = Paths.get(uploadDir);

            // 디렉토리가 존재하지 않으면 생성
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 새 파일 업로드 처리
                String originalFileName = file.getOriginalFilename();
                log.info("파일이름 확인용===={}", originalFileName);  // 파일 이름 출력
                String uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                // 파일을 저장하고, UUID 파일명을 썸네일로 설정
                Files.write(filePath, file.getBytes());
                thumbnailFileName = uuidFileName; // 새 파일명으로 설정

            } catch (IOException e) {
                model.addAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                model.addAttribute("loc", "/student/mypage"); // 수정 페이지로 리다이렉트
                return "utility/message";  // 메시지 페이지로 리턴
            }
        }

        Long userId = user.getUserId();

        // userSignUpDTO에 썸네일 파일명과 userId 설정
        userSignUpDTO.setUserThumbnail(thumbnailFileName);

        log.info("파일이름 확인용===={}", userSignUpDTO.getUserThumbnail());  // 수정된 썸네일 파일명 출력
        userSignUpDTO.setUserId(userId);  // userId는 Long 그대로 사용

        // 유저 업데이트 서비스 호출
        boolean userUpdate = userService.adminUserUpdate(userSignUpDTO);

        // 유저 정보가 성공적으로 업데이트 되면
        if (userUpdate) {
            model.addAttribute("msg", "학생 개인정보가 수정되었습니다.");
            model.addAttribute("loc", "/student/mypage");  // 수정된 후 이동할 페이지
            return "utility/message";  // 메시지 페이지로 리턴
        } else {
            // 유저 정보 수정 실패 시
            model.addAttribute("msg", "학생 개인정보 수정에 실패했습니다.");
            model.addAttribute("loc", "/student/mypage");  // 수정 페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }



    //학생 비밀번호 변경하는 곳
    @GetMapping("/student/pw/change")
    public String getPwChangeck(){
        return "student/users/pwchange";
    }


    //학생 비밀번호 비밀번호 변경 받는 곳
    @PostMapping("/student/pw/change")
    public String postPwChangeck(UserPassWordDTO userPassWordDTO, @AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();
        userPassWordDTO.setUserId(userId);

        // 비밀번호 변경 검증
        boolean isPasswordChanged = userService.password(userPassWordDTO);

        if (!isPasswordChanged) {
            // 비밀번호가 틀린 경우
            model.addAttribute("msg", "현재 비밀번호가 틀렸습니다.");
            return "student/users/pwchange";  // 비밀번호 변경 화면으로 돌아감
        }

        // 비밀번호 변경이 성공한 경우
        model.addAttribute("msg", "비밀번호가 변경되었습니다.");
        model.addAttribute("loc", "/student/mypage");  // 관리자 마이페이지로 리디렉션
        return "utility/message";  // 메시지 페이지로 리턴
    }



    //강의에 대한 리뷰  쓰는 곳
    @GetMapping("/student/course/review")
    public String getStudentCourseReview(@AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // Add sessions to the model so they can be accessed in the view
        model.addAttribute("sessions", sessions);

        return "student/KDT/coursereview";
    }

    @PostMapping("/student/course/review")
    public String postStudentCourseReview(Model model, KDTSessionEvalDTO kdtSessionEvalDTO, @AuthenticationPrincipal UserEntity user) {

        // userId를 가져오기
        Long userId = user.getUserId();

        // 강의 리뷰 저장 서비스 호출
        int reviewSaved = studentService.saveCourseReview(kdtSessionEvalDTO, userId);

        // 리뷰 저장 후 메시지 설정
        if (reviewSaved == 3) {
            model.addAttribute("msg", "강의 리뷰가 등록되었습니다.");
            model.addAttribute("loc", "/student/mypage");  // 리뷰 등록 후 마이페이지로 리디렉션
        } else if (reviewSaved == 1) {
            model.addAttribute("msg", "회차에 등록되지 않았습니다.");
            model.addAttribute("loc", "/student/mypage");  // 실패 시 다시 리뷰 페이지로 리디렉션
        } else if (reviewSaved == 2) {
            model.addAttribute("msg", "이미 리뷰가 등록되었습니다.");
            model.addAttribute("loc", "/student/mypage");  // 실패 시 다시 리뷰 페이지로 리디렉션
        } else {
            model.addAttribute("msg", "강의 리뷰 등록에 실패했습니다.");
            model.addAttribute("loc", "/student/mypage");  // 실패 시 다시 리뷰 페이지로 리디렉션
        }

        return "utility/message";  // 메시지 페이지로 리턴
    }


    //회차에 등록되지 않으면 회차가 보이지 않음
    @GetMapping("/student/main/mypage")
    public String studentMainMypage(@AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기

        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }

        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가


        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        return "student/users/mainmypage";
    }


//    //학생이 자료실 게시판 보는 곳임 - 다른 카테고리는 못봄
    @GetMapping("/student/KDT/{sid}/board/materiallist")
    public String getMaterialList(
@PathVariable Long sid,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "") String searchValue,
    @RequestParam(defaultValue = "title") String searchType, // 검색 옵션을 추가로 받기
    Model model, @AuthenticationPrincipal UserEntity user) {



        if(!studentPermissionService.hasAccessToSession(sid)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }


        Long sessionid = sid; // URL에서 받은 sessionId 설정


        Long userId = user.getUserId();

        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // 첫 번째 세션의 ID를 가져오기 (세션이 있을 경우)
        Long sessionId = sessions.isEmpty() ? null : sessions.get(0).getKdtSessionId();

        // 모델에 필요한 속성 추가
        model.addAttribute("sessionId", sessionId);  // 첫 번째 세션 ID
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true



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
        model.addAttribute("hasNext", kdtBoardMaterialPage.hasNext());
        model.addAttribute("hasPrevious", kdtBoardMaterialPage.hasPrevious());
        model.addAttribute("noResults", noResults);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("sessionsDetail", sessionsDetail);
        return "student/KDT/board/kdtmateriallist";  // 뷰 이름 반환
    }

    //학생이 자료실 보는 곳
    @GetMapping("/student/KDT/{sessionid}/board/{boardId}")
    public String getBoardDetail(@PathVariable Long sessionid,
                                 @PathVariable Long boardId,
                                 @AuthenticationPrincipal UserEntity user,
                                 Model model) {

        if(!studentPermissionService.hasAccessToSession(sessionid)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기

        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }
        boardService.reviewcount(boardId);


        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가

        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        KDTBoardviewDTO boardDetails = boardService.getBoardDetails(sessionid, boardId, KDTBoardCategory.MATERIAL);
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보
        model.addAttribute("boardDetails", boardDetails);
        return "student/KDT/board/kdtmaterialdetail";
    }





    //회차에 등록되지 않으면 회차가 보이지 않음
    @GetMapping("/student/KDT/list")
    public String studentSeMainMypage(@AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기

        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }

        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
        model.addAttribute("sessions", sessions);  // 세션 정보를 모델에 추가
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        return "student/users/sessionDetail";
    }




}
