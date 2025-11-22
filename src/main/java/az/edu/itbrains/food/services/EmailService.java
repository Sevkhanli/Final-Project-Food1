package az.edu.itbrains.food.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ⚠️ Qeyd: Göndərən e-poçt ünvanını DÜZGÜN QEYD EDİN
    private static final String FROM_EMAIL = "sevxanli77@gmail.com";

    // Əvvəlki tapşırıqlardan qalma OTP mail metodu
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject("Hesabınızı Təsdiqləyin: Təsdiqləmə Kodu");
        message.setText("Salam,\n\nHesabınızı aktivləşdirmək üçün təsdiqləmə kodunuz: " + otpCode +
                "\n\nBu kod 5 dəqiqə ərzində etibarlıdır. Hörmətlə,\nFF Restaurant Komandası");
        mailSender.send(message);
    }

    /**
     * Rezervasiyanın statusuna uyğun olaraq müştəriyə mail göndərir.
     */
    public void sendReservationStatusEmail(String toEmail, String customerName, String status, String date, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);

        String subject;
        String emailBody;

        if (status.equals("TESDIQLENIB")) {
            subject = "Rezervasiyanız Təsdiqləndi! 🎉";
            emailBody = String.format(
                    "Hörmətli %s,\n\n" +
                            "%s tarixi, saat %s üçün etdiyiniz rezervasiya **uğurla təsdiqlənmişdir**.\n\n" +
                            "Sizi restoranımızda səbirsizliklə gözləyirik. Əlavə suallarınız üçün bizimlə əlaqə saxlaya bilərsiniz.\n\n" +
                            "Hörmətlə,\nFF Restaurant Komandası",
                    customerName, date, time
            );
        } else if (status.equals("Legv_Edilib")) {
            subject = "Rezervasiyanız Ləğv Edildi";
            emailBody = String.format(
                    "Hörmətli %s,\n\n" +
                            "Təəssüflər olsun ki, %s tarixi, saat %s üçün etdiyiniz rezervasiya təsdiqlənə bilmədi.\n" +
                            "Zəhmət olmasa, başqa bir tarix üçün rezervasiya etməyə çalışın.\n\n" +
                            "Hörmətlə,\nFF Restaurant Komandası",
                    customerName, date, time
            );
        } else {
            // GOZLEMEDE statusu üçün (ilkin göndəriş)
            subject = "Rezervasiya Sorğunuz Qəbul Edildi";
            emailBody = String.format(
                    "Hörmətli %s,\n\n" +
                            "%s tarixi, saat %s üçün etdiyiniz rezervasiya sorğusu qəbul edilmişdir.\n" +
                            "Sorğunuz tezliklə nəzərdən keçiriləcək və statusu yenilənəndə sizə məlumat veriləcək.\n\n" +
                            "Hörmətlə,\nFF Restaurant Komandası",
                    customerName, date, time
            );
        }

        message.setSubject(subject);
        message.setText(emailBody);

        mailSender.send(message);
    }
}