package az.edu.itbrains.food.controllers;

import az.edu.itbrains.food.DTOs.request.UserDTO.RegisterDTO;
import az.edu.itbrains.food.services.IUserService; // Interfeysin adı

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model importu əlavə olunub
import org.springframework.web.bind.annotation.*; // Bütün lazımi annotasiyalar üçün

@Controller
@RequiredArgsConstructor // userService üçün
public class UserController {

    // İnterfeys üzərindən işləmək daha yaxşıdır
    private final IUserService userService;

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // Form üçün boş bir DTO obyekti View-a ötürülməlidir
        model.addAttribute("registerDTO", new RegisterDTO());
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerDTO") RegisterDTO registerDTO, Model model) {
        try {
            // 1. Qeydiyyatı həyata keçir
            userService.registerUser(registerDTO);

            // 2. Uğurlu qeydiyyatdan sonra "Login" səhifəsinə yönləndir
            return "redirect:/login";

        } catch (RuntimeException e) {
            // 3. Xəta baş verərsə (məsələn, Email artıq var)

            // İstifadəçiyə göstərmək üçün xəta mesajını modelə əlavə et
            model.addAttribute("error", e.getMessage());

            // Xəta mesajı ilə birlikdə qeydiyyat səhifəsinə geri dön
            return "/register";
        }
    }
}