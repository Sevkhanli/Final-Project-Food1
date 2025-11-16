package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.response.FooterSettingDTO;
import az.edu.itbrains.food.services.IFooterSettingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminFooterSettingController {

    private final IFooterSettingService footerService;

    // Bütün admin controllerlər üçün cari URI-ni təmin edir
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // 1. Footer məlumatını redaktə səhifəsinə gətir (READ)
    @GetMapping("/footer-settings")
    public String editFooterSettings(Model model) {
        FooterSettingDTO settings = footerService.getSettings();

        // Əgər qeyd yoxdursa, yeni boş bir DTO yaradırıq
        if (settings == null) {
            // QEYD: DTO konstruktorunda id-ni təyin etmək risklidir,
            // çünki bu, DB-də qeyd olmasa belə Hibernate-in UPDATE etməsinə səbəb ola bilər.
            // Biz Service-də ID=1-i yeniləyəcək şəkildə düzəltməliyik.
            settings = new FooterSettingDTO(1L, "© 2025 Food. Bütün hüquqlar qorunur.", "", "", "", "");
            model.addAttribute("isNew", true);
        } else {
            model.addAttribute("isNew", false);
        }

        model.addAttribute("footerSettings", settings);

        // ⭐ DÜZƏLİŞ BURADADIR: Yeni View Yolu
        return "dashboard/footer-settings/footer-settings-edit";
    }

    // 2. Məlumatı yenilə və ya yarat (SAVE)
    @PostMapping("/footer-settings/save")
    public String saveFooterSettings(@ModelAttribute("footerSettings") FooterSettingDTO dto,
                                     RedirectAttributes redirectAttributes) {

        footerService.saveOrUpdateSettings(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Footer məlumatları uğurla yeniləndi!");

        return "redirect:/admin/footer-settings";
    }
}