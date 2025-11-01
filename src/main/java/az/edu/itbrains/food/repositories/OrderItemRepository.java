package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository; // 🛑 İMPORT ƏLAVƏ EDİN

@Repository // 🛑 REPOSITORY ANNOTASİYASINI ƏLAVƏ EDİN
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 🛑 YENİ METOD: Ən çox satılan məhsulun ID-sini qaytarır
    @Query(value = "SELECT oi.menu_item_id " +
            "FROM order_items oi " +
            "GROUP BY oi.menu_item_id " +
            "ORDER BY SUM(oi.quantity) DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Long findTopSellingMenuItemId();
}