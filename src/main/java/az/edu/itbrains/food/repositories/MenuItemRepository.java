package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

    // 1. Kateqoriyaya görə axtarış: YALNIZ AKTİV MƏHSULLARI GƏTİRİR.
    // Əvvəlki: findByCategory_Id(Long categoryId);
    List<MenuItem> findByCategory_IdAndIsActiveTrue(Long categoryId);

    // 2. İlk N məhsulun gətirilməsi: YALNIZ AKTİV MƏHSULLARDAN İLK N-i GƏTİRİR.
    // Native Query istifadə etdiyiniz üçün (MySQL8Dialect istifadə edirsinizsə bu işləyəcək):
    @Query(value = "SELECT * FROM menu_item WHERE is_active = TRUE LIMIT :limit", nativeQuery = true)
    List<MenuItem> findFirstNActive(@Param("limit") int limit); // Adını findFirstNActive ilə dəyişdim

    // VƏ YA Daha təmiz JPA yolu (Əgər native query problem yaratsa):
    // List<MenuItem> findTopByIsActiveTrue(int limit);

    // 3. Aktiv olan bütün MenuItem-lərin sayını hesablayır (Bu metod düzgündür)
    long countByIsActiveTrue();

    // 4. Bütün menyu məhsulları: YALNIZ AKTİV MƏHSULLARI GƏTİRİR.
    // Bu metod artıq mövcuddur, lakin adını dəyişdirmirəm (sizdə findByIsActiveTrue adlanır).
    List<MenuItem> findByIsActiveTrue();
}