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
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;

    // 🏆 SADƏCƏ BU SAHƏNİ SAXLAYIN: 'active' yox, 'isActive' istifadə edin.
    @Column(name = "is_active") // Database-dəki adı
    private Boolean isActive = true; // Java-da 'Boolean' istifadə edirik

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 🛑 QEYD: Əvvəlki kodunuzdakı 'private boolean isActive = true;' və 'private Boolean active;'
    // sahələrini SİLİN. Yalnız yuxarıdakı 'private Boolean isActive = true;' qalsın.
}