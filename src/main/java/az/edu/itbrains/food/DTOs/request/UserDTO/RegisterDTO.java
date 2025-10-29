package az.edu.itbrains.food.DTOs.request.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // @Getter, @Setter, @ToString və @EqualsAndHashCode-u ehtiva edir
public class RegisterDTO {

    @NotEmpty(message = "Ad sahəsi boş ola bilməz")
    private String name;

    @NotEmpty(message = "Soyad sahəsi boş ola bilməz")
    private String surname;

    @NotEmpty(message = "Email sahəsi boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    private String email;

    @NotEmpty(message = "Şifrə sahəsi boş ola bilməz")
    @Size(min = 6, message = "Şifrə ən az 6 simvol olmalıdır")
    private String password;

    @NotEmpty(message = "Şifrə təsdiqi boş ola bilməz")
    private String confirmPassword;
}
