package az.edu.itbrains.food.DTOs.DashboardDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MenuItemCreateDTO {

    @NotBlank(message = "Ad sahəsi boş ola bilməz.")
    private String name;

    @NotBlank(message = "Təsvir sahəsi boş ola bilməz.")
    private String description;

    @NotNull(message = "Qiymət sahəsi boş ola bilməz.")
    @Positive(message = "Qiymət müsbət dəyər olmalıdır.")
    private Double price;

    // Şəkil URL-i (Hələ file upload işləmədiyimiz üçün string saxlayırıq)
    @NotBlank(message = "Şəkil URL-i boş ola bilməz.")
    private String imageUrl;

    @NotNull(message = "Kateqoriya seçilməlidir.")
    // Kateqoriyanı ID ilə əlaqələndirəcəyik
    private Long categoryId;

    // Yeni məhsul əlavə ediləndə default olaraq aktiv olsun (Və ya formda checkbox ola bilər)
    private Boolean isActive = true;
}