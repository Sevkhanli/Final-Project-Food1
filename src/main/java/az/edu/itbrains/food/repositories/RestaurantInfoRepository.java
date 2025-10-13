package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.RestaurantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantInfoRepository extends JpaRepository<RestaurantInfo, Long> {
}
