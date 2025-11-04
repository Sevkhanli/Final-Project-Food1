package az.edu.itbrains.food.DTOs.DashboardDTO; // və ya DTO-larınızın yerləşdiyi qovluq

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MenuItemEditDTO {

    @NotNull(message = "ID boş ola bilməz.")
    @Positive(message = "ID müsbət dəyər olmalıdır.")
    private Long id;

    @NotBlank(message = "Ad sahəsi boş ola bilməz.")
    private String name;

    @NotBlank(message = "Təsvir sahəsi boş ola bilməz.")
    private String description;

    @NotNull(message = "Qiymət sahəsi boş ola bilməz.")
    @Positive(message = "Qiymət müsbət dəyər olmalıdır.")
    private Double price;

    @NotBlank(message = "Şəkil URL-i boş ola bilməz.")
    private String imageUrl;

    @NotNull(message = "Kateqoriya seçilməlidir.")
    private Long categoryId;

    private Boolean isActive = true;
}