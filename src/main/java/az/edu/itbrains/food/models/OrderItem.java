package az.edu.itbrains.food.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "menu_item_id")
    private Long menuItemId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;
    @ManyToOne
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false) // ID-dən istifadə edir
    private MenuItem menuItem;
}