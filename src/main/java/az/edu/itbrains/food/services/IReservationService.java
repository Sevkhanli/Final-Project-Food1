package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.models.Reservation;

public interface IReservationService {


    void createReservation(ReservationRequestDTO reservationRequestDTO);
}