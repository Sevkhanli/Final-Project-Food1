package az.edu.itbrains.food.services;

import az.edu.itbrains.food.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter; // Bu importu əlavə etdim, çünki aşağıda istifadə olunur

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Yuxarıdakı kodu təmizləmək üçün statik fieldlar yaratdım
    private static final String FROM_EMAIL = "sevxanli77@gmail.com";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


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


    // --- YENİ METOD 1: Sifariş Təsdiqi (Sadə Mətn) ---

    /**
     * İlkin sifariş təsdiqi üçün SimpleMailMessage göndərir.
     */
    public void sendOrderConfirmationEmail(String recipientEmail, Order order) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            System.err.println("Xəbərdarlıq: E-poçt ünvanı boş olduğu üçün sifariş təsdiqi göndərilmədi.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(recipientEmail);
        message.setSubject("Sifarişiniz Uğurla Qəbul Edildi! (#" + order.getId() + ")");

        String orderDetails = String.format(
                "Hörmətli %s,\n\n" +
                        "Sizin #%d nömrəli sifarişiniz uğurla qəbul edildi və hazırlandıqdan sonra göndəriləcək.\n\n" +
                        "--- Sifariş Məlumatları ---\n" +
                        "ID: #%d\n" +
                        "Tarix: %s\n" +
                        "Ümumi Qiymət: %.2f AZN\n" +
                        "Çatdırılma Ünvanı: %s\n" +
                        "Əlaqə Nömrəsi: %s\n\n" +
                        "Təxmini çatdırılma müddəti: 30-45 dəqiqə.\n" +
                        "Hörmətlə,\nSobetim Komandası",
                order.getFullName(),
                order.getId(),
                order.getId(),
                order.getOrderDate().format(DATE_TIME_FORMATTER), // Yuxarıda yaratdığımız formatter istifadə olunur
                order.getTotalPrice(),
                order.getAddress(),
                order.getPhoneNumber()
        );

        message.setText(orderDetails);

        try {
            mailSender.send(message);
            System.out.println("Sifariş təsdiqlənməsi maili uğurla göndərildi: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("Sifariş təsdiqlənməsi maili göndərilərkən xəta: " + e.getMessage());
        }
    }


    // --- DÜZƏLİŞ EDİLMİŞ METOD: Status Yenilənməsi ---

    /**
     * Sifarişin statusu dəyişəndə müştəriyə mətn formatında bildirim göndərir.
     */
    public void sendOrderStatusUpdateEmail(String toEmail, Long orderId, String newStatus, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);

        String subject;
        String emailBody;

        // Statusa uyğun məzmunu təyin edirik
        switch (newStatus.toUpperCase()) {
            case "HAZIRLANIR":
                subject = "Sifarişiniz Hazırlanır (#" + orderId + ") 🧑‍🍳";
                emailBody = String.format(
                        "Hörmətli %s,\n\n" +
                                "Sizin #%d nömrəli sifarişiniz **hazırlanma mərhələsinə keçdi**.\n" +
                                "Yeməkləriniz tezliklə yola çıxmaq üçün hazırlanacaq.\n\n" +
                                "Hörmətlə,\nFF Komandası",
                        fullName, orderId
                );
                break;

            case "YOLDADIR": // ⭐ Sizin Admin panelinizdən gələn dəqiq statusu tanıyır.
            case "YOLA ÇIXDI":
                subject = "Sifarişiniz Yola Çıxdı! 🛵";
                emailBody = String.format(
                        "Hörmətli %s,\n\n" +
                                "Sizin #%d nömrəli sifarişiniz **artıq ünvana yola çıxdı**.\n" +
                                "Kuryer qısa müddətdə sizdə olacaq. Zəhmət olmasa, əlaqə nömrəniz (telefon) açıq olsun.\n\n" +
                                "Hörmətlə,\nFF Komandası",
                        fullName, orderId
                );
                break;

            case "ÇATDIRILDI":
                subject = "Sifarişiniz Uğurla Çatdırıldı! ✅";
                emailBody = String.format(
                        "Hörmətli %s,\n\n" +
                                "Sizin #%d nömrəli sifarişiniz **uğurla çatdırıldı**.\n" +
                                "Bizi seçdiyiniz üçün təşəkkür edirik. Növbəti sifarişlərdə görüşənədək!\n\n" +
                                "Hörmətlə,\nFF Komandası",
                        fullName, orderId
                );
                break;

            case "LƏĞV EDİLDİ": // ⭐ Ləğv edilmə maili əlavə edildi
                subject = "Sifarişiniz Ləğv Edildi ❌";
                emailBody = String.format(
                        "Hörmətli %s,\n\n" +
                                "Təəssüf ki, bəzi problemlərə görə #%d nömrəli sifarişiniz **ləğv edildi**.\n" +
                                "Ödənişiniz qısa müddət ərzində geri qaytarılacaqdır.\n" +
                                "Yaranan narahatlığa görə üzr istəyirik. Hər hansı bir sualınız yaranarsa, zəhmət olmasa bizimlə əlaqə saxlayın.\n\n" +
                                "Hörmətlə,\nFF Komandası",
                        fullName, orderId
                );
                break;

            default:
                // YENİ və ya tanınmayan digər statuslar üçün bildiriş göndərmirik
                return;
        }

        message.setSubject(subject);
        message.setText(emailBody);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Status yenilənməsi maili göndərilərkən xəta: " + e.getMessage());
        }
    }
}