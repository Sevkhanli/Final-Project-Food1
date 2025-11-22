package az.edu.itbrains.food.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sizin_gmail_adresiniz@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Hesabınızın Təsdiqlənməsi Kodu");

        String emailBody = String.format(
                "Hörmətli istifadəçi,\n\nSizin qeydiyyatınızı tamamlamaq üçün OTP (One-Time Password) kodunuz:\n\n%s\n\nBu kod 5 dəqiqə ərzində etibarlıdır.",
                otpCode
        );
        message.setText(emailBody);

        mailSender.send(message);
    }
}