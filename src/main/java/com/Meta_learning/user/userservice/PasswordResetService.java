package com.Meta_learning.user.userservice;

import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String EMAIL_SUBJECT = "메타러닝 비밀번호 찾기 인증";
    private static final String SENDER_EMAIL = "pyth0n9q@gmail.com";
    private static final String SENDER_NAME = "배움의 시작 메타러닝";

    // 이메일로 비밀번호 리셋 처리
    public boolean resetPassword(String name, LocalDate userBirth, String email) {
        // 이메일, 이름, 생년월일로 사용자 찾기
        Optional<UserEntity> userOptional = userRepository.findByUserEmailAndNameAndUserBirth(email, name, userBirth);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            // 랜덤 비밀번호 생성
            String newPassword = generateRandomPassword();

            try {
                sendNewPasswordEmail(email, newPassword); // 실제 랜덤 비밀번호를 이메일로 전송
            } catch (MessagingException e) {
                return false;
            }

            // 비밀번호를 해싱하여 저장
            String encodedPassword = passwordEncoder.encode(newPassword);

            // userId와 userPw만 업데이트
            user.updateUserIdAndPassword(user.getUserId(), encodedPassword); // 수정된 메서드 활용

            userRepository.save(user); // 변경된 사용자 정보 저장

            return true;
        } else {
            return false;
        }
    }

    // 랜덤 비밀번호 생성
    // 랜덤 비밀번호 생성
    private String generateRandomPassword() {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";

        String allChars = upperCaseChars + lowerCaseChars + digits + specialChars;  // 모든 문자 집합
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // 최소 1개의 특수문자 추가
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 나머지 7글자 랜덤으로 생성 (대문자, 소문자, 숫자, 특수문자 모두 포함)
        String remainingChars = upperCaseChars + lowerCaseChars + digits;
        for (int i = 1; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 비밀번호를 섞어서 무작위 순서로 배열
        String generatedPassword = password.toString();
        StringBuilder shuffledPassword = new StringBuilder();
        while (generatedPassword.length() > 0) {
            int randomIndex = random.nextInt(generatedPassword.length());
            shuffledPassword.append(generatedPassword.charAt(randomIndex));
            generatedPassword = generatedPassword.substring(0, randomIndex) + generatedPassword.substring(randomIndex + 1);
        }

        return shuffledPassword.toString();
    }
    // 새 비밀번호 이메일로 발송
    private void sendNewPasswordEmail(String email, String newPassword) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(email);
            helper.setSubject(EMAIL_SUBJECT);

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;'>" +
                    "<div style='max-width: 650px; margin: 30px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>" +
                    "    <h2 style='color: #FF7F32; text-align: center; font-size: 28px; margin-bottom: 20px;'>새 비밀번호 안내</h2>" +
                    "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>안녕하세요, 메타러닝 서비스입니다!</p>" +
                    "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>아래의 새 비밀번호로 로그인 부탁드립니다. 로그인 후 비밀번호를 변경 해주시기 바랍니다.</p>" +
                    "    <div style='text-align: center; padding: 15px 30px; background-color: #FF7F32; border-radius: 8px; margin: 20px auto; width: fit-content;'>" +
                    "        <p style='color: #ffffff; font-size: 20px; margin: 0;'>새 비밀번호: <strong>" + newPassword + "</strong></p>" +
                    "    </div>" +
                    "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>감사합니다.<br>메타러닝 서비스 드림</p>" +
                    "    <hr style='border: none; border-top: 1px solid #eaeaea; margin: 20px 0;'>" +
                    "    <p style='font-size: 14px; color: #888888; text-align: center;'>" +
                    "        © 2025 메타러닝 서비스<br>" +
                    "        <a href='https://kotoki-service.com' style='color: #FF7F32; text-decoration: none;'>메타러닝 서비스 웹사이트</a>" +
                    "    </p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Unsupported encoding in email sender information", e);
        }
    }
}
