package az.edu.itbrains.food.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import az.edu.itbrains.food.enums.ReservationStatus;

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

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "reservation_date")
    private LocalDate reservationDate;
    @Column(name = "reservation_time")
    private LocalTime reservationTime;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    // ⭐ DÜZƏLİŞ: String yerinə Enum və default dəyər
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status = ReservationStatus.GOZLEMEDE;
}