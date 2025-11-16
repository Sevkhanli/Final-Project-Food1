package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.enums.ReservationStatus;
import az.edu.itbrains.food.models.Customer;
import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.repositories.CustomerRepository;
import az.edu.itbrains.food.repositories.ReservationRepository;
import az.edu.itbrains.food.services.IReservationService;
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
        // ⭐ DÜZƏLİŞ: Enum dəyəri təyin edilir
        reservation.setStatus(ReservationStatus.GOZLEMEDE);

        reservationRepository.save(reservation);
    }

    // ⭐ YENİ METOD: Bütün reservasiyaları gətirmək
    @Override
    public List<Reservation> getAllReservations() {
        Sort sort = Sort.by(
                Sort.Order.desc("reservationDate"), // Ən yeni tarix yuxarıda
                Sort.Order.desc("reservationTime")  // Eyni tarix üçün ən yeni saat yuxarıda
        );
        return reservationRepository.findAll(sort); // Bu metod çağırılmalıdır!
            }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // ⭐ YENİ METOD: Statusu yeniləmək
    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
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