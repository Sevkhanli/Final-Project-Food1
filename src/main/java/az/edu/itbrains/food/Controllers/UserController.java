package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.enums.Status;
import az.edu.itbrains.food.services.IUserService;
import az.edu.itbrains.food.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final OtpService otpService;

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerDTO") RegisterDTO registerDTO, Model model) {
        try {
            userService.registerUser(registerDTO);
            return "redirect:/verify-otp?email=" + registerDTO.getEmail();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "/register";
        }
    }

    @GetMapping("/verify-otp")
    public String getOtpVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "/otp-verification-page";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otpCode, Model model) {
        try {
            if (otpService.validateOtp(email, otpCode)) {
                // Təsdiqləmə uğurlu oldu
                userService.updateUserStatusByEmail(email, Status.AKTİV);
                return "redirect:/login?verified";
            } else {
                // Təsdiqləmə uğursuz oldu (Yanlış Kod və ya Vaxtı Keçib)
                model.addAttribute("error", "Yanlış və ya vaxtı keçmiş təsdiqləmə kodu!");
                model.addAttribute("email", email);
                return "/otp-verification-page";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Təsdiqləmə zamanı xəta: " + e.getMessage());
            model.addAttribute("email", email);
            return "/otp-verification-page";
        }
    }
}