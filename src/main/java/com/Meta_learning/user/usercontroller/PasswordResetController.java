package com.Meta_learning.user.usercontroller;

import com.Meta_learning.user.userdto.PasswordResetRequestDTO;
import com.Meta_learning.user.userservice.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/main/check-pw")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        String name = passwordResetRequestDTO.getName();
        String userBirthString = passwordResetRequestDTO.getBirth();
        String userEmail = passwordResetRequestDTO.getEmail();

        // String을 LocalDate로 변환
        LocalDate userBirth = convertStringToLocalDate(userBirthString);

        // 이름, 생년월일, 이메일을 통해 사용자 확인 후 비밀번호 변경 및 이메일 발송
        boolean isResetSuccessful = passwordResetService.resetPassword(name, userBirth, userEmail);

        if (isResetSuccessful) {
            return ResponseEntity.ok("새 비밀번호가 이메일로 전송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("입력한 정보가 일치하지 않습니다. 다시 시도해주세요.");
        }
    }

    // String을 LocalDate로 변환하는 메서드
    private LocalDate convertStringToLocalDate(String birthString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(birthString, formatter);
    }
}
