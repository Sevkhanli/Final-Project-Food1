package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

    List<MenuItem> findByCategory_IdAndIsActiveTrue(Long categoryId);

    @Query(value = "SELECT * FROM menu_item WHERE is_active = TRUE LIMIT :limit", nativeQuery = true)
    List<MenuItem> findFirstNActive(@Param("limit") int limit); // Adını findFirstNActive ilə dəyişdim


    long countByIsActiveTrue();


    List<MenuItem> findByIsActiveTrue();
}