package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.enums.ReservationStatus;
import az.edu.itbrains.food.models.Customer;
import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.repositories.CustomerRepository;
import az.edu.itbrains.food.repositories.ReservationRepository;
import az.edu.itbrains.food.services.IReservationService;
import az.edu.itbrains.food.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void createReservation(ReservationRequestDTO reservationRequestDTO) {

        Customer customer = customerRepository.findByEmail(reservationRequestDTO.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(reservationRequestDTO.getCustomerName());
                    newCustomer.setEmail(reservationRequestDTO.getCustomerEmail());
                    newCustomer.setPhoneNumber(reservationRequestDTO.getCustomerPhoneNumber());
                    return customerRepository.save(newCustomer);
                });

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setReservationDate(reservationRequestDTO.getReservationDate());
        reservation.setReservationTime(reservationRequestDTO.getReservationTime());
        reservation.setNumberOfPeople(reservationRequestDTO.getNumberOfPeople());
        reservation.setStatus(ReservationStatus.GOZLEMEDE);

        reservationRepository.save(reservation);

        // Yeni rezervasiya yaradılanda "GÖZLƏMƏDƏ" maili göndərilir
        emailService.sendReservationStatusEmail(
                customer.getEmail(),
                customer.getName(),
                ReservationStatus.GOZLEMEDE.name(),
                reservation.getReservationDate().toString(),
                reservation.getReservationTime().toString()
        );
    }

    @Override
    public List<Reservation> getAllReservations() {
        Sort sort = Sort.by(
                Sort.Order.desc("reservationDate"),
                Sort.Order.desc("reservationTime")
        );
        return reservationRepository.findAll(sort);
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // ⭐ ƏSAS MƏNTİQ BURADADIR: Status dəyişəndə mail göndərmək
    @Override
    @Transactional
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            // Yalnız status həqiqətən dəyişibsə və təsdiq/ləğv statusuna keçibsə mail göndər
            if (reservation.getStatus() != status &&
                    (status == ReservationStatus.TESDIQLENIB || status == ReservationStatus.LEGV_EDILIB)) { // 🏆 DÜZƏLİŞ: LEGV_EDILIB istifadə olunur

                // Maili göndər (Təsdiqləndi və ya Ləğv edildi)
                emailService.sendReservationStatusEmail(
                        reservation.getCustomer().getEmail(),
                        reservation.getCustomer().getName(),
                        status.name(),
                        reservation.getReservationDate().toString(),
                        reservation.getReservationTime().toString()
                );
            }

            reservation.setStatus(status);
            return reservationRepository.save(reservation);
        }
        return null;
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}