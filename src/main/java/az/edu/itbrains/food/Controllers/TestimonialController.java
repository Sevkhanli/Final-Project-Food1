package az.edu.itbrains.food.Controllers;

import az.edu.itbrains.food.models.Testimonial;
import az.edu.itbrains.food.services.ITestimonialService;
import jakarta.validation.Valid; // 💡 Bu import lazımdır
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 💡 Model importu əlavə edin
import org.springframework.validation.BindingResult; // 💡 Bu import lazımdır
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TestimonialController {

    private final ITestimonialService testimonialService;

    @PostMapping("/testimonials")
    public String addTestimonial(@Valid @ModelAttribute("newTestimonial") Testimonial newTestimonial,
                                 BindingResult bindingResult,
                                 Model model) { // 💡 Model əlavə etdik

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {

            return "index"; // Index səhifəsinə geri qayıt, lakin bu dəfə xəta mesajları görünəcək.
        }

        testimonialService.save(newTestimonial);

        return "redirect:/";
    }
}