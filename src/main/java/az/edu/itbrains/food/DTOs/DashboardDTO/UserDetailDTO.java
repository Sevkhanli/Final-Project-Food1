package az.edu.itbrains.food.DTOs.DashboardDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDetailDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber; // Əgər User Entity-yə əlavə edilsə
    private String roleName; // Cari tək rol (rol dəyişmə üçün)
    private String status; // Hələlik sabit
    private LocalDateTime registrationDate;
}