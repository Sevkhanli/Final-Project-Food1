package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.response.RestaurantInfoResponseDTO;
import az.edu.itbrains.food.services.IRestaurantInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestaurantInfoController {

    private final IRestaurantInfoService infoService;
    private final Long RESTAURANT_INFO_ID = 1L; // Adətən tək bir qeyd olur, ID=1 götürürük

    // Bütün admin controllerlər üçün cari URI-ni təmin edir
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // 1. Məlumatı Redaktə Səhifəsinə Gətir (READ)
    @GetMapping("/restaurant-info")
    public String editRestaurantInfo(Model model) {
        RestaurantInfoResponseDTO info = infoService.getRestaurantInfo(RESTAURANT_INFO_ID);

        if (info == null) {
            // Əgər qeyd yoxdursa, yeni boş bir DTO yaradırıq (Create funksiyası üçün)
            info = new RestaurantInfoResponseDTO(null, "Restoran Adı", "Qısa Təsvir...");
            model.addAttribute("isNew", true);
        } else {
            model.addAttribute("isNew", false);
        }

        model.addAttribute("restaurantInfo", info);
        return "dashboard/settings/restaurant-info-edit";
    }

    // 2. Məlumatı Yenilə və ya Yarat (UPDATE/CREATE)
    @PostMapping("/restaurant-info/save")
    public String saveRestaurantInfo(@ModelAttribute("restaurantInfo") RestaurantInfoResponseDTO dto,
                                     @RequestParam(value = "isNew", required = false) boolean isNew,
                                     RedirectAttributes redirectAttributes) {

        RestaurantInfoResponseDTO result;
        if (isNew) {
            // Qeyd yoxdursa, yarat
            result = infoService.createRestaurantInfo(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Restoran məlumatı **uğurla yaradıldı**.");
        } else {
            // Qeyd varsa, yenilə
            result = infoService.updateRestaurantInfo(RESTAURANT_INFO_ID, dto);
            if (result != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Restoran məlumatı **uğurla yeniləndi**.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Yeniləmə zamanı xəta baş verdi.");
            }
        }

        // Yenidən redaktə səhifəsinə yönləndiririk
        return "redirect:/admin/restaurant-info";
    }
}