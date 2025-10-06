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
@Table(name = "special_offers")
public class SpecialOffers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(name = "discount_Percentage")
    private double discountPercentage;
    // ...
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "menu_item_id_1") // Əsas məhsul ID-si
    private Long menuItemId1;

    @Column(name = "menu_item_id_2") // İkinci məhsul ID-si
    private Long menuItemId2;
}