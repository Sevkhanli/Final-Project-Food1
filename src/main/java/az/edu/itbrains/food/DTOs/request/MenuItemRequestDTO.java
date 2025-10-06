package az.edu.itbrains.food.DTOs.request;

import az.edu.itbrains.food.models.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRequestDTO {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
}