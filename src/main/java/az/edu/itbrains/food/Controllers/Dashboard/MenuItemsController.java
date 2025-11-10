package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemCreateDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemEditDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.ICategoryService;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/menu-items") // Bütün metodlar /admin/menu-items altından işləyəcək
public class MenuItemsController {

    private final IMenuItemService menuItemService;
    private final ICategoryService categoryService;

    // Bütün məhsulların siyahısını göstərən səhifə
    @GetMapping
    public String getAllMenuItems(Model model) {

        // 1. Servisdən bütün məhsulları (DTO formatında) çəkirik
        List<MenuItemResponseDTO> items = menuItemService.getAllMenuItem();

        // 2. Bu siyahını View-a ötürürük
        model.addAttribute("menuItems", items);
        model.addAttribute("currentUri", "/admin/menu-items"); // Sidebar aktivliyini təmin etmək üçün

        // 3. View səhifəsinə yönləndiririk
        return "dashboard/menu-items/index";
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {

        // 1. Yeni boş DTO obyekti yaradırıq.
        model.addAttribute("menuItem", new MenuItemCreateDTO());

        model.addAttribute("categories", categoryService.getAllCategory());
        // 2. Sidebar aktivliyini təmin etmək üçün
        model.addAttribute("currentUri", "/admin/menu-items/create");

        // 3. Template-ə yönləndiririk
        return "dashboard/menu-items/create";
    }

    @PostMapping("/create")
    public String saveMenuItem(
            @Valid @ModelAttribute("menuItem") MenuItemCreateDTO dto,
            BindingResult bindingResult,
            Model model,
            // Loggers istifadə etmədiyiniz üçün, bu funksionallığı saxlayırıq
            RedirectAttributes redirectAttributes) {

        // 1. Validasiya Xətaları
        if (bindingResult.hasErrors()) {
            System.err.println("!!! Validasiya Xətası: Forma məlumatları səhvdir.");
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("currentUri", "/admin/menu-items/create");
            return "dashboard/menu-items/create";
        }

        // 2. Servis/Database Əməliyyatı
        try {
            // Əsas əməliyyat
            menuItemService.createMenuItem(dto);

            // Uğur mesajı ilə siyahı səhifəsinə yönləndir
            redirectAttributes.addFlashAttribute("successMessage", "Yeni məhsul uğurla əlavə edildi!");
            return "redirect:/admin/menu-items";

        } catch (RuntimeException e) {
            // Bazada və ya Servisdə xəta baş verərsə
            System.err.println("!!! Bazaya yazılma xətası: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Məhsul əlavə edilərkən xəta baş verdi: " + e.getMessage());

            // Xəta halında siyahı səhifəsinə yönləndir ki, xəta mesajı görünsün.
            return "redirect:/admin/menu-items";
        }
    }
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        MenuItemEditDTO editDTO = menuItemService.getMenuItemForEdit(id);

        // Kateqoriyaları yenə də Modela əlavə edirik (Dropdown üçün)
        model.addAttribute("categories", categoryService.getAllCategory());

        model.addAttribute("menuItem", editDTO);
        model.addAttribute("currentUri", "/admin/menu-items");

        return "dashboard/menu-items/edit";
    }
    @PostMapping("/{id}/edit")
    public String updateMenuItem(
            // URL-dən ID-ni qəbul edirik (Validasiya üçün lazım olmasa da, URL uyğunluğu üçün saxlanılır)
            @PathVariable Long id,

            // DTO-nu qəbul edir və Validasiyadan keçirir
            @Valid @ModelAttribute("menuItem") MenuItemEditDTO dto,

            // Validasiya nəticələrini tutmaq üçün
            BindingResult bindingResult,

            Model model) {

        // 1. Əgər Validasiya xətaları varsa, Formu yenidən göstər
        if (bindingResult.hasErrors()) {
            // Formu yenidən göstərərkən Kateqoriyalar yenidən Modela əlavə edilməlidir
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("currentUri", "/admin/menu-items");
            return "dashboard/menu-items/edit";
        }

        // 2. Validasiya uğurlu olarsa, Servis vasitəsilə database-i yenilə
        try {
            menuItemService.updateMenuItem(dto);
        } catch (RuntimeException e) {
            // Məsələn, Kateqoriya tapılmadı xətası
            model.addAttribute("errorMessage", "Məhsul yenilənərkən xəta baş verdi: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("currentUri", "/admin/menu-items");
            return "dashboard/menu-items/edit";
        }

        // 3. Uğurla yeniləndikdən sonra Məhsullar siyahısı səhifəsinə yönləndir
        return "redirect:/admin/menu-items?success=updated";
    }
    @GetMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Model əvəzinə RedirectAttributes istifadə edirik

        try {
            menuItemService.deleteMenuItem(id);
            // Uğur mesajı
            redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla silindi.");
        } catch (RuntimeException e) {
            // Xəta mesajı
            redirectAttributes.addFlashAttribute("errorMessage", "Silinmə zamanı xəta: " + e.getMessage());
        }

        // RedirectAttributes ilə yönləndiririk
        return "redirect:/admin/menu-items";
    }

}