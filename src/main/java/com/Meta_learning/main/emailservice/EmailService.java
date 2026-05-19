package com.Meta_learning.main.emailservice;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, VerificationDetails> emailVerificationStore = new HashMap<>();

    // Default expiry time for verification code is 5 minutes
    private static final int VERIFICATION_CODE_EXPIRY_MINUTES = 5;
    private static final String SENDER_EMAIL = "pyth0n9q@gmail.com";
    private static final String EMAIL_SUBJECT = "메타러닝 회원가입 이메일 인증";

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // 랜덤한 인증번호 생성 (대소문자, 숫자, 특수문자 포함, 8자리)
    public String generateVerificationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            code.append(characters.charAt(randomIndex));  // 랜덤 문자 선택
        }
        return code.toString();
    }

    // 이메일로 인증번호 보내기
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("To email address must not be null or empty");
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(SENDER_EMAIL, "배움의 시작 메타러닝");
            helper.setTo(toEmail);  // 수신자 이메일 주소
            helper.setSubject(EMAIL_SUBJECT);

            // 이메일 본문 내용 생성
            String emailBody = createEmailBody(verificationCode);

            helper.setText(emailBody, true);  // HTML 형식으로 이메일 내용 전송

            javaMailSender.send(message);  // 이메일 전송

            // 저장된 인증 코드 및 만료 시간 설정
            emailVerificationStore.put(toEmail, new VerificationDetails(verificationCode, LocalDateTime.now()));
            logger.info("이메일 인증 코드가 {}에게 전송되었습니다.", toEmail);
        } catch (Exception e) {
            logger.error("이메일 발송에 실패했습니다.", e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    // 이메일 인증 코드 검증
    public boolean verifyEmailCode(String email, String inputCode) {
        VerificationDetails details = emailVerificationStore.get(email);

        if (details == null) {
            return false;  // 인증 코드가 없으면 검증 실패
        }

        // 인증 코드가 만료되지 않았는지 확인 (5분 이내)
        if (details.getTimestamp().plusMinutes(VERIFICATION_CODE_EXPIRY_MINUTES).isBefore(LocalDateTime.now())) {
            emailVerificationStore.remove(email);  // 만료된 코드 삭제
            logger.warn("인증 코드가 만료되었습니다: {}", email);
            return false;
        }

        // 입력된 코드와 저장된 코드가 일치하는지 확인
        return details.getVerificationCode().equals(inputCode);
    }

    // 인증 코드 정보를 담는 클래스
    private static class VerificationDetails {
        private final String verificationCode;
        private final LocalDateTime timestamp;

        public VerificationDetails(String verificationCode, LocalDateTime timestamp) {
            this.verificationCode = verificationCode;
            this.timestamp = timestamp;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    // 이메일 본문 내용 생성 (회원가입 인증번호 HTML 템플릿)
    // 이메일 본문 내용 생성 (회원가입 인증번호 HTML 템플릿)
    private String createEmailBody(String verificationCode) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;'>"
                + "<div style='max-width: 650px; margin: 30px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
                + "    <h2 style='color: #FF7F32; text-align: center; font-size: 28px; margin-bottom: 20px;'>회원가입 인증번호</h2>"
                + "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>안녕하세요, 메타러닝 서비스입니다!</p>"
                + "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>회원가입을 위한 인증번호를 아래에서 확인하실 수 있습니다. </p>"
                + "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>해당 인증번호를 입력하여 회원가입을 완료해 주세요.</p>"
                + "    <div style='text-align: center; padding: 15px 30px; background-color: #FF7F32; border-radius: 8px; margin: 20px 0; width: fit-content; max-width: 350px; margin-left: auto; margin-right: auto;'>"
                + "        <p style='font-size: 26px; font-weight: bold; color: #ffffff; margin: 0;'>"
                + verificationCode
                + "        </p>"
                + "    </div>"
                + "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>인증번호는 5분 이내에 사용해야 하며, 만약 인증번호가 만료된 경우 다시 요청해 주세요.</p>"
                + "    <p style='font-size: 16px; color: #333333; line-height: 1.6;'>감사합니다! 메타러닝 서비스를 이용해 주셔서 감사합니다.</p>"
                + "    <div style='text-align: center; margin-top: 30px; font-size: 14px; color: #888888;'>"
                + "        <p>© 2025 메타러닝 서비스</p>"
                + "        <p><a href='https://kotoki-service.com' style='color: #FF7F32; text-decoration: none;'>메타러닝 서비스 웹사이트</a></p>"
                + "    </div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }


}
