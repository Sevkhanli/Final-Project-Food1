package az.edu.itbrains.food.DTOs.request.UserDTO;

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
public class LoginDTO {
    @NotEmpty(message = "Email sahəsi boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    private String email;
    @NotEmpty(message = "Şifrə sahəsi boş ola bilməz")
    @Size(min = 6, message = "Şifrə ən az 6 simvol olmalıdır")
    private String password;

}