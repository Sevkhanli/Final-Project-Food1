package az.edu.itbrains.food.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TestimonialResponseDTO {
    private Long id;
    private String customerName;
    private String comment;
}
