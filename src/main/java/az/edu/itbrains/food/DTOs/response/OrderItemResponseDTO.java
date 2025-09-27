package az.edu.itbrains.food.DTOs.response;

import az.edu.itbrains.food.models.MenuItem;
import az.edu.itbrains.food.models.Order;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private int quantity;
    private double price;
    private String order;
    private String menuItem;
}
