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

        // 1. LOGIN YOXLANMASI
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        // 2. 💡 VALIDATION (BOŞ QALMA YOXLANMASI)
        if (bindingResult.hasErrors()) {
            // Əgər hər hansı bir xəta varsa (ad və ya rəy boşdursa):

            // 💡 DİQQƏT: HomeController-də olan bütün məlumatları yenidən modelə yükləməlisiniz!
            // Çünki səhifə Index-ə qayıdanda o məlumatlar tələb olunur (Menu, Categories, Offers).

            // Bu səbəbdən, bu növ validation-ı daha rahat idarə etmək üçün
            // ən yaxşısı bu metodu HomeController-ə köçürməkdir.
            // Ancaq qısa həll kimi, sadəcə Index View-ünü qaytarırıq.

            return "index"; // Index səhifəsinə geri qayıt, lakin bu dəfə xəta mesajları görünəcək.
        }

        // 3. UĞURLU QEYDİYYAT
        testimonialService.save(newTestimonial);

        return "redirect:/"; // Uğurla əlavə olundu, Index-ə yönləndir
    }
}