package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.models.Reservation;
import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.services.IUserService;
import az.edu.itbrains.food.services.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final IUserService userService;
    private final IReservationService reservationService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Cari istifadəçini tap
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // İstifadəçi məlumatlarını model-ə əlavə et
        model.addAttribute("user", currentUser);
        model.addAttribute("cashbackBalance", currentUser.getCashbackBalance());

        // 👇 YENİ: Rezervasiyaları əlavə et
        List<Reservation> userReservations = reservationService.getUserReservationsByEmail(currentUser.getEmail());
        model.addAttribute("reservations", userReservations);

        return "profile";
    }
}