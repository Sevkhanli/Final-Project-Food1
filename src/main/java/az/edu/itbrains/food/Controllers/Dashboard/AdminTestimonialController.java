package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.response.TestimonialResponseDTO;
import az.edu.itbrains.food.services.ITestimonialService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminTestimonialController {

    private final ITestimonialService testimonialService;

    // Bütün admin controllerlər üçün cari URI-ni təmin edir (Naviqasiyanı aktiv etmək üçün)
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        // Məsələn, /admin/testimonials gələcək
        return request.getRequestURI();
    }

    // 1. Rəylərin siyahısını göstərmək (READ)
    @GetMapping("/testimonials")
    public String listTestimonials(Model model) {
        // Service-dən TestimonialResponseDTO listini gətirir
        List<TestimonialResponseDTO> testimonials = testimonialService.getAll();

        // ⭐ QEYD: Ən yenisini üstdə görmək üçün, service qatında sıralama əlavə etmək tövsiyə olunur.
        // Hələ ki, Repository-nin default sıralaması ilə gedirik.

        model.addAttribute("testimonials", testimonials);
        return "dashboard/testimonial/index";
    }

    // 2. Rəyi silmək (DELETE)
    @GetMapping("/testimonials/delete/{id}")
    public String deleteTestimonial(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            testimonialService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Rəy #" + id + " uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rəyi silərkən xəta baş verdi.");
        }
        return "redirect:/admin/testimonials";
    }
}