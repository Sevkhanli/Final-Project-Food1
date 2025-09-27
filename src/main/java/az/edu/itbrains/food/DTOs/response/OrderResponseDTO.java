package az.edu.itbrains.food.DTOs.response;

import az.edu.itbrains.food.models.User;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {     //TODO(Sifariş)
    private Long id;
    private double totalPrice;
    private String users;

}
