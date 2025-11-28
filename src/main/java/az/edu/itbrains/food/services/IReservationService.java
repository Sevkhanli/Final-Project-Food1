package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.enums.ReservationStatus;
import java.util.List;

public interface IReservationService {

    void createReservation(ReservationRequestDTO reservationRequestDTO);

    // ⭐ YENİ METODLAR: Admin Panel üçün
    List<Reservation> getAllReservations();
    Reservation getReservationById(Long id); // Detal/Redaktə üçün
    Reservation updateReservationStatus(Long id, ReservationStatus status);
    void deleteReservation(Long id); // Silmə əməliyyatı üçün
    List<Reservation> getUserReservationsByEmail(String email);

}