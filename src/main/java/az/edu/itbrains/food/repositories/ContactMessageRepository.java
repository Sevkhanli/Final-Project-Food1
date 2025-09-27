package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> {
}
