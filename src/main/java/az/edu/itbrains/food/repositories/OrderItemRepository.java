package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
