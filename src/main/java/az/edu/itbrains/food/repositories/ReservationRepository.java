package az.edu.itbrains.food.repositories;

import az.edu.itbrains.food.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

        List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);
    }

