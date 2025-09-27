package az.edu.itbrains.food.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private int productId;
    private String productName;
    private double price;
    private int quantity;
}
