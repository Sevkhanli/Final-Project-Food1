package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface OrderRepository extends JpaRepository<Order,Long> {
}