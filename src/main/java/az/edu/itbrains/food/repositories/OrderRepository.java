package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. BU GÜN SİFARİŞ SAYI: Başlanğıc vaxtından sonraki sifarişləri sayır
    // :startOfToday dəyişəni OrderServiceImpl-dən gələcək (Bugünün 00:00:00-ı)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startOfToday")
    long countOrdersSince(LocalDateTime startOfToday);

    // 2. BU GÜN GƏLİR: Başlanğıc vaxtından sonraki sifarişlərin totalPrice cəmini hesablayır
    // SUM() nəticəsi 'double' deyil, 'Double' kimi gəlir, bu səbəbdən Long istifadə edirik.
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDate >= :startOfToday")
    Double sumTotalPriceSince(LocalDateTime startOfToday);

    List<Order> findTop5ByOrderByOrderDateDesc();
}