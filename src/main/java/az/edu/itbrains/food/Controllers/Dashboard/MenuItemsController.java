package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.MenuItemCreateDTO;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO;
import az.edu.itbrains.food.services.ICategoryService;
import az.edu.itbrains.food.services.IMenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            // DTO-nu qəbul edir və Validasiyadan keçirir
            @Valid @ModelAttribute("menuItem") MenuItemCreateDTO dto,

            // Validasiya nəticələrini tutmaq üçün
            BindingResult bindingResult,

            Model model) {

        // 1. Əgər Validasiya xətaları varsa, Formu yenidən göstər
        if (bindingResult.hasErrors()) {
            // Kateqoriyalar yenidən Modela əlavə edilməlidir ki, dropdown dolmuş qalsın
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("currentUri", "/admin/menu-items/create");
            return "dashboard/menu-items/create";
        }

        // 2. Validasiya uğurlu olarsa, Servis vasitəsilə database-ə yaz
        try {
            menuItemService.createMenuItem(dto);
        } catch (Exception e) {
            // Hər hansı bir database xətası olarsa (məsələn, Unikal sahə), formu yenidən göstər
            model.addAttribute("errorMessage", "Məhsul əlavə edilərkən xəta baş verdi: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("currentUri", "/admin/menu-items/create");
            return "dashboard/menu-items/create";
        }

        // 3. Uğurla yadda saxlandıqdan sonra Məhsullar siyahısı səhifəsinə yönləndir
        return "redirect:/admin/menu-items?success=created";
    }
}