package az.edu.itbrains.food.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İstifadəçinin Email adresi
    @Column(nullable = false, unique = true)
    private String userEmail;

    // Yaranmış OTP kodu
    @Column(nullable = false, length = 6)
    private String otpCode;

    // Kodun bitmə vaxtı
    @Column(nullable = false)
    private LocalDateTime expiryTime;
}