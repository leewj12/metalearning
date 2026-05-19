package com.Meta_learning.main.mainrestcontroller;


import com.Meta_learning.main.emailservice.EmailService;
import com.Meta_learning.main.emailservice.EmailVerificationDTO;
import com.Meta_learning.user.userdto.UserSignUpDTO;
import com.Meta_learning.user.userservice.UserService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainRestController {

    private final UserService userService;
    private final EmailService emailService;

    // 이메일 중복 체크 (POST 방식)
    @PostMapping("/check-email")
    public ResponseEntity<ResponseMessage> checkEmail(@RequestBody UserSignUpDTO signUpDTO) {
        boolean isEmailAvailable = userService.isEmailAvailable(signUpDTO.getUserEmail());
        ResponseMessage responseMessage;
        if (isEmailAvailable) {
            responseMessage = new ResponseMessage("success", "사용 가능한 이메일입니다.");
            return ResponseEntity.ok(responseMessage);
        } else {
            responseMessage = new ResponseMessage("error", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }
    }

    // 이메일 인증 코드 발송 (POST 방식)
    @PostMapping("/send-verification-email")
    public ResponseEntity<ResponseMessage> sendVerificationEmail(@RequestBody EmailVerificationDTO emailVerificationDTO) {
        String email = emailVerificationDTO.getUserEmail();

        if (email == null || email.trim().isEmpty()) {
            ResponseMessage responseMessage = new ResponseMessage("fail", "이메일 주소가 비어 있습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }

        boolean isEmailAvailable = userService.isEmailAvailable(email);  // 이메일 중복 확인
        if (!isEmailAvailable) {
            ResponseMessage responseMessage = new ResponseMessage("fail", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);  // 400 오류로 이메일 중복 처리
        }

        try {
            String verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationEmail(email, verificationCode);  // 이메일 발송
            ResponseMessage responseMessage = new ResponseMessage("success", "인증 이메일이 전송되었습니다.");
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            ResponseMessage responseMessage = new ResponseMessage("error", "인증 이메일 전송 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);  // 500 오류로 전송 실패 메시지 전달
        }
    }

    // 이메일 인증 코드 검증 (POST 방식)
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseMessage> verifyEmail(@RequestBody EmailVerificationDTO emailVerificationDTO) {
        String email = emailVerificationDTO.getUserEmail();
        String inputCode = emailVerificationDTO.getVerificationCode();

        if (email == null || email.trim().isEmpty() || inputCode == null || inputCode.trim().isEmpty()) {
            ResponseMessage responseMessage = new ResponseMessage("fail", "이메일과 인증 코드를 모두 입력해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }

        boolean isVerified = emailService.verifyEmailCode(email, inputCode);  // 인증 코드 검증

        ResponseMessage responseMessage;
        if (isVerified) {
            responseMessage = new ResponseMessage("success", "이메일 인증이 완료되었습니다.");
            return ResponseEntity.ok(responseMessage);
        } else {
            responseMessage = new ResponseMessage("error", "인증 코드가 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }
    }
}
