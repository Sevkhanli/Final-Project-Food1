package az.edu.itbrains.food.DTOs.response;

import az.edu.itbrains.food.models.Category;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private Boolean isActive;
    private String imageUrl;
    private String category;
}
