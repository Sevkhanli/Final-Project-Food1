package az.edu.itbrains.food.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {
        private String customerName;
        private String customerEmail;
        private String customerPhoneNumber;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate reservationDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
        private LocalTime reservationTime;

        private int numberOfPeople;
    }

