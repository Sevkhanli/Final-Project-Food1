package az.edu.itbrains.food.DTOs.DashboardDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserListDTO {
    private Long id;
    private String fullName; // Ad Soyad (User Entity-dəki getFullName()-dən gələcək)
    private String email;
    private String phoneNumber; // Əgər User Entity-yə əlavə edilsə
    private String roleNames; // Birləşmiş rollar ("ADMIN, MÜŞTƏRİ")
    private String status; // Hələlik sabit ("AKTİV")
    private LocalDateTime registrationDate; // Qeydiyyat tarixi (Əgər User Entity-yə əlavə edilsə)
}
