package az.edu.itbrains.food.services;

import az.edu.itbrains.food.models.Otp;
import az.edu.itbrains.food.repositories.OtpRepository;
import az.edu.itbrains.food.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Transactional
    public void generateAndSendOtp(String userEmail) {
        // Köhnə qeydi tapıb silir
        otpRepository.findByUserEmail(userEmail).ifPresent(otpRepository::delete);

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        Otp otp = new Otp();
        otp.setUserEmail(userEmail);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(expiryTime);

        otpRepository.save(otp);

        emailService.sendOtpEmail(userEmail, otpCode);
    }

    @Transactional
    public boolean validateOtp(String userEmail, String otpCode) {

        // 🏆 findByEmailAndCodeForValidation metodu yalnız həm email, həm də kod uyğun gələndə nəticə qaytarır.
        Optional<Otp> otpRecord = otpRepository.findByEmailAndCodeForValidation(userEmail, otpCode);

        if (otpRecord.isPresent()) {
            Otp otp = otpRecord.get();

            // Kod tapıldı: İndi vaxtı yoxla
            if (otp.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otp);
                return true;
            } else {
                otpRepository.delete(otp);
                return false;
            }
        }
        // Kod bazada tapılmadı (yanlış kod daxil edilib)
        return false;
    }
}