package az.edu.itbrains.food.DTOs.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDetailDTO {
    private String productName;
    private Integer quantity; // Integer olaraq istifadə etmək daha təhlükəsizdir
    private BigDecimal unitPrice;
}