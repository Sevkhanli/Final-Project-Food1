package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.request.ReservationRequestDTO;
import az.edu.itbrains.food.services.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// ƏLAVƏ ETMƏLİ OLDUĞUNUZ İMPORT:
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/book")
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping
    public String showReservationForm(Model model) {
        // Redirektdən gələn flash məlumatlar (mesaj, vaxt) Model-ə avtomatik əlavə olunur.

        // Əgər DTO Model-də yoxdursa (ilkin GET sorğusu) yeni DTO əlavə edirik
        if (!model.containsAttribute("reservationDto")) {
            model.addAttribute("reservationDto", new ReservationRequestDTO());
        }
        return "book";
    }

    @PostMapping("/create")
    public String createReservation(@ModelAttribute("reservationDto") ReservationRequestDTO reservationRequestDTO,
                                    RedirectAttributes redirectAttributes) { // Model yerinə RedirectAttributes istifadə edirik
        try {
            reservationService.createReservation(reservationRequestDTO);

            // Məlumatları Redirect zamanı View-ə ötürürük:
            redirectAttributes.addFlashAttribute("message", "Rezervasiyanız uğurla qəbul edildi!");
            redirectAttributes.addFlashAttribute("reservationDate", reservationRequestDTO.getReservationDate());
            redirectAttributes.addFlashAttribute("reservationTime", reservationRequestDTO.getReservationTime());

            return "redirect:/book"; // PRG (Post/Redirect/Get) patterni istifadə edirik

        } catch (Exception e) {
            // Xəta halında da mesajı ötürə bilərik
            redirectAttributes.addFlashAttribute("error", "Rezervasiya zamanı xəta baş verdi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("reservationDto", reservationRequestDTO);

            return "redirect:/book";
        }
    }}