package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ✅ YENİ METOD: Məhsul ID-sinə bağlı olan bütün sifariş elementlərini silir
    @Modifying // DML (DELETE/UPDATE) əməliyyatları üçün məcburidir
    @Transactional // Tranzaksiya tələb edir
    void deleteByMenuItemId(Long menuItemId);

    @Query(value = "SELECT oi.menu_item_id " +
            "FROM order_items oi " +
            "GROUP BY oi.menu_item_id " +
            "ORDER BY SUM(oi.quantity) DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Long findTopSellingMenuItemId();
}