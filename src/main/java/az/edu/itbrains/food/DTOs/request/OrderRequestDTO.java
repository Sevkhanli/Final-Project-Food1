package az.edu.itbrains.food.DTOs.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    @NotEmpty(message = "Ad və Soyad mütləqdir.")
    @Size(min = 3, message = "Ad ən az 3 simvol olmalıdır.")
    private String fullName;

    @NotEmpty(message = "Əlaqə Nömrəsi mütləqdir.")
    private String phoneNumber;

    @NotEmpty(message = "Ünvan mütləqdir.")
    @Size(min = 5, message = "Ünvan ən az 5 simvol olmalıdır.")
    private String address;

    // 👇 YENİ SAHƏ: Anonim sifarişçilər üçün
    @NotEmpty(message = "E-poçt ünvanı mütləqdir.")
    @Email(message = "Düzgün e-poçt formatı daxil edin.")
    private String email;
}