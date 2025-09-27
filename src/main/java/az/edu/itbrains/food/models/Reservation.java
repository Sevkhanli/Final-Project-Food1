package az.edu.itbrains.food.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One əlaqəsi: Bir müştərinin birdən çox rezervasiyası ola bilər
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "reservation_date")
    private LocalDate reservationDate;
    @Column(name = "reservation_time")
    private LocalTime reservationTime;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    private String status; // 'pending', 'confirmed', 'cancelled'
}
