package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.models.User;
import az.edu.itbrains.food.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final IUserService userService;

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

        return "profile"; // profile.jsp və ya profile.html
    }
}