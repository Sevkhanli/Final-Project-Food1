package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.UserDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.UserListDTO;
import az.edu.itbrains.food.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;
import java.util.Arrays; // availableStatuses üçün əlavə edildi

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users") // Bütün metodlar /admin/users ilə başlayacaq
public class AdminUserController {

    private final IUserService userService;

    // 1. İstifadəçilərin Siyahısı və Axtarışı (GET: /admin/users)
    @GetMapping
    public String listUsers(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "q", required = false) String query) {

        model.addAttribute("currentUri", request.getRequestURI());

        List<UserListDTO> users;
        String title = "Bütün İstifadəçilər";

        if (query != null && !query.trim().isEmpty()) {
            users = userService.searchUsers(query.trim());
            model.addAttribute("query", query.trim());
            title = "Axtarış Nəticələri";
        } else {
            users = userService.getAllUsersForAdminList();
        }

        model.addAttribute("users", users);
        model.addAttribute("pageTitle", title);

        // ⭐ Dinamik saylar service-dən çəkilir (Fərz edirik ki, Service tərəfi tamamlanıb)
        model.addAttribute("totalUsersCount", userService.countAllUsers());
        model.addAttribute("adminUsersCount", userService.countAdminUsers());
        model.addAttribute("activeUsersCount", userService.countActiveUsers());
        model.addAttribute("blockedUsersCount", userService.countBlockedUsers());

        model.addAttribute("pageTitle", "Bütün İstifadəçilər");
        return "dashboard/user-list/index";
    }

    // 2. İstifadəçi Detalları (GET: /admin/users/details/{id})
    @GetMapping("/details/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());

        UserDetailDTO userDetails = userService.getUserDetailsById(id);

        if (userDetails == null) {
            return "redirect:/admin/users";
        }

        Set<String> allRoles = userService.getAllRoleNames();

        model.addAttribute("user", userDetails);
        model.addAttribute("availableRoles", allRoles);

        // availableStatuses üçün List.of() yerinə Arrays.asList() istifadə edək, daha uyğundur.
        // Həmçinin, Enum istifadə olunursa (əvvəlki cavabda təklif olundu) bu daha təmiz olar.
        model.addAttribute("availableStatuses", Arrays.asList("AKTİV", "BLOKLANIB"));
        model.addAttribute("currentRole", userDetails.getRoleName());

        return "dashboard/user-list/user-details";
    }

    // 3. Rol Yenilənməsi (POST: /admin/users/update-role)
    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam("newRole") String newRole,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(userId, newRole);
            redirectAttributes.addFlashAttribute("successMessage", "İstifadəçinin rolu uğurla dəyişdirildi: " + newRole);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rol yenilənərkən xəta baş verdi: " + e.getMessage());
        }
        return "redirect:/admin/users/details/" + userId;
    }

    // 4. Status Yenilənməsi (POST: /admin/users/update-status)
    @PostMapping("/update-status")
    public String updateUserStatus(@RequestParam("userId") Long userId,
                                   @RequestParam("newStatus") String newStatus,
                                   RedirectAttributes redirectAttributes) {
        try {
            // ⭐ Gerçək service metodunu çağırırıq
            userService.updateUserStatus(userId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "İstifadəçinin statusu uğurla dəyişdirildi: " + newStatus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Status yenilənərkən xəta baş verdi: " + e.getMessage());
        }
        return "redirect:/admin/users/details/" + userId;
    }
}