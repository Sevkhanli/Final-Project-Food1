package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.models.Customer;
import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.repositories.CustomerRepository;
import az.edu.itbrains.food.repositories.ReservationRepository;
import az.edu.itbrains.food.services.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;


    private final CustomerRepository customerRepository;



    @Transactional
    public void createReservation(ReservationRequestDTO reservationRequestDTO) {
        // 1. Müştəri məlumatlarını yoxla və ya yenisini yarat
        Customer customer = customerRepository.findByEmail(reservationRequestDTO.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(reservationRequestDTO.getCustomerName());
                    newCustomer.setEmail(reservationRequestDTO.getCustomerEmail());
                    newCustomer.setPhoneNumber(reservationRequestDTO.getCustomerPhoneNumber());
                    return customerRepository.save(newCustomer);
                });

        // 2. Yeni rezervasiyanı yarat
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setReservationDate(reservationRequestDTO.getReservationDate());
        reservation.setReservationTime(reservationRequestDTO.getReservationTime());
        reservation.setNumberOfPeople(reservationRequestDTO.getNumberOfPeople());
        reservation.setStatus("pending");

        // 3. Verilənlər bazasına yaz
        reservationRepository.save(reservation);
    }
}



