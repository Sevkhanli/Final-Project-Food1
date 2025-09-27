package az.edu.itbrains.food.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String message;
    private LocalDateTime sentAt;
}
