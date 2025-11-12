package az.edu.itbrains.food.DTOs.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDTO { // 👈 YENİ AD

    private Long id;
    private double totalPrice;

    // ⭐ Admin Siyahısı üçün Əsas Sahələr ⭐
    private LocalDateTime orderDate; // Sifarişin tarixi
    private String orderStatus;      // Status
    private String fullName;         // Müştərinin adı
    private String phoneNumber;      // Əlaqə nömrəsi
    private String address;          // Çatdırılma ünvanı
}