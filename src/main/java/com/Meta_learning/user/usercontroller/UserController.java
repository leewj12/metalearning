package com.Meta_learning.user.usercontroller;

import com.Meta_learning.course.courseservice.InstrService;
import com.Meta_learning.course.courseservice.requset.InstrCreateServiceRequest;
import com.Meta_learning.user.usercontroller.dto.request.InstrUpRequest;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final InstrService instrService;

    //회원가입 폼 이동
    @GetMapping("/signup")
    public String getSingup() {
        return "users/signup";
    }

    //회원가입 제출
    @PostMapping("/signup")
    public String postSignUp(UserSignUpDTO userSignUpDTO, Model model) {
        // 회원가입 처리

        boolean userSave = userService.usersave(userSignUpDTO);

        if (userSave) {
            // 회원가입 성공 시
            model.addAttribute("msg", "회원가입이 완료되었습니다. 축하합니다!");
            model.addAttribute("loc", "/login");  // 홈으로 리디렉션
        } else {
            // 회원가입 실패 시
            model.addAttribute("msg", "회원가입이 실패되었습니다.");
            model.addAttribute("loc", "/signup");  // 회원가입 페이지로 리디렉션
        }

        return "utility/message";
    }


    //로그인화면
    @GetMapping("/login")
    public String getLogin() {
        return "users/login";
    }

    // 관리자가 유저 특정 사용자의 상세 정보를 가져오는 GET 요청 처리
    @GetMapping("/admin/users/{userId}")
    public String getUserById(@PathVariable Long userId, Model model) {
        // 사용자 ID로 정보를 가져오는 서비스 호출
        UserSignUpDTO user = userService.getUserById(userId);

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (user != null) {
            model.addAttribute("user", user);
            return "admin/users/userdetail";  // userDetail.html 뷰 반환
        } else {
         // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/admin/users/list");  // 회원 목록 페이지로 리디렉션
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }
    }

    //관리자가 유저 상세정보 수정하는 곳
    @GetMapping("/admin/users/update/{userId}")
    public String getUserUpdate(@PathVariable("userId") Long userId, Model model) {
        // 사용자 ID로 정보를 가져오는 서비스 호출
        UserSignUpDTO user = userService.getUserById(userId);

        // 사용자 정보가 있을 경우, 모델에 추가하고 userDetail 페이지 반환
        if (user != null) {
            model.addAttribute("user", user);
            return "admin/users/update";  // userDetail.html 뷰 반환
        } else {
            // 사용자 정보가 없으면, 관리자 페이지로 리다이렉트
            model.addAttribute("msg", "유저 정보가 없습니다.");
            model.addAttribute("loc", "/view/admin/users/list");  // 회원 목록 페이지로 리디렉션
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }
    }


//관리자가 자기 계정 수정했을 때
    @PostMapping("/admin/mypage/update")
    public String postAdminUserUpdate(
            @AuthenticationPrincipal UserEntity user,
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
                String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/admin"; // 서버 경로 확인 필요
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
//                    if (deleted) {
//                        log.info("기존 이미지 파일이 삭제되었습니다.");
//                    } else {
//                        log.error("기존 이미지 파일 삭제 실패.");
//                    }
                }
            }

            // 2. 새 파일 업로드 처리
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/admin"; // 서버 경로 확인 필요
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
                model.addAttribute("loc", "/admin/mypage"); // 수정 페이지로 리다이렉트
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
            model.addAttribute("msg", "관리자 개인정보가 수정되었습니다.");
            model.addAttribute("loc", "/admin/mypage");  // 수정된 후 이동할 페이지
            return "utility/message";  // 메시지 페이지로 리턴
        } else {
            // 유저 정보 수정 실패 시
            model.addAttribute("msg", "관리자 개인정보 수정에 실패했습니다.");
            model.addAttribute("loc", "/admin/mypage");  // 수정 페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }

    //관리자가 유저 개인 정보 수정하는 메서드
    @PostMapping("/admin/users/update/{userId}")
    public String postUserUpdate(
            @PathVariable("userId") Long userId,  // userId를 Long 타입으로 받기
            Model model,
            UserSignUpDTO userSignUpDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("existingThumbnail") String existingThumbnail) {

        String userRole = userService.getUserRole(userId);  // 유저 권한 조회

        // 기존 썸네일 파일명을 기본으로 설정
        String thumbnailFileName = existingThumbnail;

        // 유저 역할에 맞는 업로드 디렉토리 설정
        String uploadDir = "";
        switch (userRole) {
            case "ADMIN":
                uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/admin";
                break;
            case "MANAGER":
                uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/manager";
                break;
            case "INSTRUCTOR":
                uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/Instr";
                break;
            case "STUDENT":
                uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/profileimages/student";
                break;
            default:
                model.addAttribute("msg", "알 수 없는 사용자 역할입니다.");
                model.addAttribute("loc", "/admin/users/update/" + userId);
                return "utility/message";
        }

        // 새 파일이 업로드 되었을 경우
        if (file != null && !file.isEmpty()) {  // 파일이 존재할 때만 처리
            // 1. 기존 이미지 삭제
            if (existingThumbnail != null && !existingThumbnail.isEmpty()) {
                Path existingFilePath = Paths.get(uploadDir, existingThumbnail);
                File existingFile = existingFilePath.toFile();

                // 기존 파일이 존재하면 삭제
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    if (!deleted) {
                        model.addAttribute("msg", "기존 이미지 파일 삭제 실패.");
                        model.addAttribute("loc", "/admin/users/update/" + userId);
                        return "utility/message";  // 메시지 페이지로 리턴
                    }
                }
            }

            // 2. 새 파일 업로드 처리
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
                thumbnailFileName = uuidFileName;  // 새 파일명으로 설정

            } catch (IOException e) {
                model.addAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                model.addAttribute("loc", "/admin/users/update/" + userId); // 수정 페이지로 리다이렉트
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
            model.addAttribute("msg", "유저 정보가 수정되었습니다.");
            model.addAttribute("loc", "/view/admin/users/list");  // 수정된 후 이동할 페이지
            return "utility/message";  // 메시지 페이지로 리턴
        } else {
            // 유저 정보 수정 실패 시
            model.addAttribute("msg", "유저 수정에 실패했습니다.");
            model.addAttribute("loc", "/admin/users/update/" + userId);  // 수정 페이지로 리다이렉트
            return "utility/message";  // 메시지 페이지로 리턴
        }
    }

    @GetMapping("/student/up")
    public String createInstrUpForm(@AuthenticationPrincipal UserEntity user, Model model) {
        // 로그인한 사용자의 userId를 가져오기
        Long userId = user.getUserId(); // UserEntity에서 userId를 가져옴

        // userId로 InstrEntity가 있는지 확인
        boolean userHasInstr = instrService.existsByUserId(userId);

        // 이미 등록된 강사인 경우
        if (userHasInstr) {
            model.addAttribute("msg", "이미 등록된 강사 입니다");
            model.addAttribute("loc", "/");  // 수정된 후 이동할 페이지 (적절한 URL로 변경 필요)
            return "utility/message";  // 메시지 페이지로 리턴
        }

        // 사용자 이름을 모델에 추가
        String name = user.getName();
        model.addAttribute("name", name);

        // 빈 InstrUpRequest 객체를 모델에 추가
        model.addAttribute("instrUpRequest", new InstrUpRequest());

        if (model.containsAttribute("message")) {
        }

        return "users/up"; // 사용자 업로드 폼 페이지로 리턴
    }









    @PostMapping("/student/up")
    public String createInstrUp(@AuthenticationPrincipal UserEntity user,
                                @Valid @ModelAttribute InstrUpRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        // 이미 강사 신청 여부 확인
        if (instrService.hasInstrUp(user)) {
            // 플래시 메시지로 알림 설정
            redirectAttributes.addFlashAttribute("message", "이미 강사 신청이 완료되었습니다.");
            return "redirect:/student/up"; // 이미 신청되어 있다면 홈으로 리다이렉트
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("name", user.getName()); // 이름 다시 추가
            return "users/up"; // 유효성 검증 실패 시 다시 폼 페이지로
        }
        InstrCreateServiceRequest instrCreateServiceRequest = request.toInstrCreateServiceRequest(user);
        instrService.createInstrUp(instrCreateServiceRequest);

        redirectAttributes.addFlashAttribute("message", "강사 신청이 완료되었습니다.");
        // 신청 완료 후 홈페이지로 이동하는 방식 개선
        return "redirect:/";
    }





}
