package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.MethodPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MethodPermissionRepository extends JpaRepository<MethodPermission, Long> {
    MethodPermission findByName(String methodName);
}
