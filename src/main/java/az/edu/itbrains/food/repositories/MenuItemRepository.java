package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {
    List<MenuItem> findByCategory_Id(Long categoryId);
    @Query(value = "SELECT * FROM menu_item LIMIT :limit", nativeQuery = true)
    List<MenuItem> findFirstN(@Param("limit") int limit);
    // 🛑 YENİ METOD: Aktiv olan bütün MenuItem-lərin sayını hesablayır
    long countByIsActiveTrue();
}