package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.enums.ReservationStatus;
import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.services.IReservationService;
import jakarta.servlet.http.HttpServletRequest; // ƏLAVƏ EDİLDİ
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminReservationController {

    private final IReservationService reservationService;

    // ⭐ DÜZƏLİŞ ƏLAVƏ EDİLDİ: Bütün Controller metodları üçün currentUri-ni Model-ə əlavə edir.
    // Bu, layout.html-də naviqasiya hissəsinin (th:classappend="${currentUri.equals(...)}") işləməsi üçün lazımdır.
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // Reservasiyalar siyahısını göstər
    @GetMapping("/reservations")
    // Bu metoda artıq Model və HttpServletRequest obyekti əlavə etməyə ehtiyac yoxdur,
    // çünki @ModelAttribute metodu avtomatik işləyir.
    public String listReservations(Model model) {

        List<Reservation> reservations = reservationService.getAllReservations();

        model.addAttribute("reservations", reservations);
        return "dashboard/reservation/index";
    }

    // Reservasiya detallarını göstər
    @GetMapping("/reservations/{id}")
    public String viewReservationDetail(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return "redirect:/admin/reservations";
        }
        model.addAttribute("reservation", reservation);
        return "dashboard/reservation/reservation-detail";
    }

    // Reservasiyanın statusunu redaktə səhifəsini göstər
    @GetMapping("/reservations/edit-status/{id}")
    public String editReservationStatus(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return "redirect:/admin/reservations";
        }
        model.addAttribute("reservation", reservation);

        List<String> statuses = Arrays.stream(ReservationStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        model.addAttribute("statuses", statuses);

        return "dashboard/reservation/reservation-edit";
    }


    // Reservasiyanın statusunu yenilə (POST)
    @PostMapping("/reservations/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ReservationStatus newStatus,
                               RedirectAttributes redirectAttributes) {

        Reservation updatedReservation = reservationService.updateReservationStatus(id, newStatus);

        if (updatedReservation != null) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Rezervasiya #" + id + " statusu **" + newStatus.getAzName() + "** olaraq yeniləndi.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Rezervasiya tapılmadı və ya yenilənmədi.");
        }

        return "redirect:/admin/reservations";
    }

    // Silmə əməliyyatı
    @GetMapping("/reservations/delete/{id}")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservationService.deleteReservation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Rezervasiya #" + id + " uğurla silindi.");
        return "redirect:/admin/reservations";
    }
}