package az.edu.itbrains.food.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "testimonials")
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 Ad sahəsi boş (və ya sadəcə boşluq) ola bilməz
    @NotBlank(message = "Ad sahəsi boş qala bilməz.")
    private String customerName;

    // 💡 Rəy sahəsi boş qala bilməz
    @NotBlank(message = "Rəy mətni boş qala bilməz.")
    private String comment;
}

