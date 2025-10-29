package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
