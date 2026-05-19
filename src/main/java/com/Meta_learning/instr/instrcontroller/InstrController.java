package com.Meta_learning.instr.instrcontroller;


import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardFileDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userdto.UserPassWordDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
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
public class InstrController {

    private final UserService userService;
    private final StaffPermissionService staffPermissionService;
    private final BoardService boardService;
    private final KDTService kdtService;

    //강사 메인페이지
    @GetMapping("/instr/main/mypage")
    public String instrMain(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userInstr = userService.getUserById(userId);

        // 유저 정보가 있으면 마이페이지로 리턴
        if (userInstr != null) {
            model.addAttribute("userInstr", userInstr);
            return "instr/users/mainmypage";  // 관리자 마이페이지로 이동
        } else {
            // 유저 정보가 없으면 메시지 표시 후 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/instr/mypage");  // 관리자 마이페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴

        }

    }

    //강사 마이페이지
    @GetMapping("/instr/mypage")
    public String instrMypage(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        // 유저 정보 가져오기
        UserSignUpDTO userInstr = userService.getUserById(userId);

        // 유저 정보가 있으면 마이페이지로 리턴
        if (userInstr != null) {
            model.addAttribute("userInstr", userInstr);
            return "instr/users/mydetail";  // 관리자 마이페이지로 이동
        } else {
            // 유저 정보가 없으면 메시지 표시 후 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/instr/mypage");  // 관리자 마이페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴

        }

    }


    //강사 마이페이지 수정하는 곳
    @GetMapping("/instr/mypage/update")
    public String getAdminMypageUpdate(@AuthenticationPrincipal UserEntity user, Model model) {

        Long userId = user.getUserId();

        UserSignUpDTO userInstr = userService.getUserById(userId);

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (userInstr != null) {
            model.addAttribute("userInstr", userInstr);  // 올바르게 괄호를 닫음
            return "instr/users/mypageupdate";  // userDetail.html 뷰 반환
        } else {
            // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/instr/mypage");  // 잘못된 문자열을 수정
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }

    }

    //강사 마이페이지 수정
    @PostMapping("/instr/mypage/update")
    public String postAdminUserUpdate(
            Model model,
            UserSignUpDTO userSignUpDTO,
            @AuthenticationPrincipal UserEntity user,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("existingThumbnail") String existingThumbnail ) {

        // 기존 썸네일 파일명을 기본으로 설정
        String thumbnailFileName = existingThumbnail;

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {  // 파일이 존재할 때만 처리
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/Instr"; // 서버 경로 확인 필요
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    // 로그는 삭제되었으므로, 더 이상 로그를 남기지 않음
                }
            }

            // 2. 새 파일 업로드 처리
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/Instr"; // 서버 경로 확인 필요
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
                model.addAttribute("loc", "/instr/mypage"); // 수정 페이지로 리다이렉트
                return "utility/message";  // 메시지 페이지로 리턴
            }
        }

        Long userId = user.getUserId();

        // userSignUpDTO에 썸네일 파일명과 userId 설정
        userSignUpDTO.setUserThumbnail(thumbnailFileName);
        userSignUpDTO.setUserId(userId);  // userId는 Long 그대로 사용

        // 유저 업데이트 서비스 호출
        boolean userUpdate = userService.adminUserUpdate(userSignUpDTO);

        // 유저 정보가 성공적으로 업데이트 되면
        if (userUpdate) {
            model.addAttribute("msg", "강사 개인정보가 수정되었습니다.");
            model.addAttribute("loc", "/instr/mypage");  // 수정된 후 이동할 페이지
            return "utility/message";  // 메시지 페이지로 리턴
        } else {
            // 유저 정보 수정 실패 시
            model.addAttribute("msg", "강사 개인정보 수정에 실패했습니다.");
            model.addAttribute("loc", "/instr/mypage");  // 수정 페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }


    //강사 비밀번호 변경하는 곳
    @GetMapping("/instr/pw/change")
    public String getPwChangeck(){
        return "instr/users/pwchange";
    }


    //강사 비밀번호 비밀번호 변경 받는 곳
    @PostMapping("/instr/pw/change")
    public String postPwChangeck(UserPassWordDTO userPassWordDTO, @AuthenticationPrincipal UserEntity user, Model model) {
        Long userId = user.getUserId();
        userPassWordDTO.setUserId(userId);

        // 비밀번호 변경 검증
        boolean isPasswordChanged = userService.password(userPassWordDTO);

        if (!isPasswordChanged) {
            // 비밀번호가 틀린 경우
            model.addAttribute("msg", "현재 비밀번호가 틀렸습니다.");
            return "instr/users/pwchange";  // 비밀번호 변경 화면으로 돌아감
        }

        // 비밀번호 변경이 성공한 경우
        model.addAttribute("msg", "비밀번호가 변경되었습니다.");
        model.addAttribute("loc", "/instr/mypage");  // 관리자 마이페이지로 리디렉션
        return "utility/message";  // 메시지 페이지로 리턴
    }



    //강사 자료실 게시판 보는 곳임 - 다른 카테고리는 못봄
    @GetMapping("/instr/KDT/{sid}/board/materiallist")
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

        return "instr/KDT/board/kdtmateriallist";  // 뷰 이름 반환
    }


    //강사 대시보드  작성하는 곳
    @GetMapping("/instr/KDT/{sid}/board")
    public String getManagerBoard(@PathVariable Long sid, Model model) {
        // 세션 ID를 PathVariable로 받아옴
        Long sessionid = sid;  // URL에서 전달된 sid 값을 사용

        // 강사인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sid)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 강사만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        // 세션 정보를 가져오기 위해 서비스 호출
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionid);

        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보 추가

        // 뷰 반환
        return "instr/KDT/board/kdtboardwriting";  // "kdtboard" 뷰를 반환
    }

    // kdt 게시판 자료 보내서  넣는 곳
    @PostMapping("/instr/KDT/{sid}/board")
    public String postEmployreviewWrite(@PathVariable("sid") Long sessionId,  // sid를 @PathVariable로 받아옴
                                        @AuthenticationPrincipal UserEntity user,
                                        @ModelAttribute KDTBoardDTO kdtboardDTO,
                                        @ModelAttribute KDTBoardFileDTO kdtboardFileDTO,
                                        @RequestParam("file") MultipartFile[] files,
                                        Model model) {


        // 강사인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 강사만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
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
            model.addAttribute("loc", "/instr/KDT/" + sessionId + "/board/materiallist");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // userDetail.html 뷰 반환
        } else {
            // 게시글 저장 실패 시 처리
            model.addAttribute("msg", "강의 자료 업로드에 실패했습니다.");
            model.addAttribute("loc", "/instr/KDT/" + sessionId + "/board");  // sessionId를 포함한 URL로 변경
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }
    }


    //자료실 상세보기하는 곳
    // 예시: 컨트롤러에서 서비스 호출
    @GetMapping("/instr/KDT/{sessionId}/board/{boardId}")
    public String getBoardDetail(@PathVariable Long sessionId,
                                 @PathVariable Long boardId,
                                 Model model) {

        if (!staffPermissionService.hasAccessToSession(sessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 강사만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }
        boardService.reviewcount(boardId);
        KDTBoardviewDTO boardDetails = boardService.getBoardDetails(sessionId, boardId, KDTBoardCategory.MATERIAL);
        KDTSessionDTO sessionsDetail = kdtService.getSessionsBySessId(sessionId);

        model.addAttribute("sessionsDetail", sessionsDetail);  // 세션 정보
        model.addAttribute("boardDetails", boardDetails);
        return "instr/KDT/board/kdtmaterialdetail";
    }

    @GetMapping("/instr/KDT/{sid}/staff/list")
    public String getStaffList(@PathVariable("sid") Long sid, Model model) {


        if (!staffPermissionService.hasAccessToSession(sid)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 강사만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 리다이렉트 위치 설정
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
        return "instr/KDT/stafflist"; // 반환할 뷰 이름
    }







}
