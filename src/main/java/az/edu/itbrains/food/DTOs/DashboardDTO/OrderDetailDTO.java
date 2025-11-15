package az.edu.itbrains.food.DTOs.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String fullName;
    private String phoneNumber;
    private String address;
    private List<OrderItemDetailDTO> orderItems;
}