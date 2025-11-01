package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.DTOs.response.MenuItemResponseDTO; // MenuItem adını çəkmək üçün DTO lazımdır
import az.edu.itbrains.food.services.IMenuItemService;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.IUserService;
import az.edu.itbrains.food.services.IOrderItemService; // Yeni Service
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final IUserService userService;
    private final IOrderService orderService;
    private final IMenuItemService menuItemService;
    private final IOrderItemService orderItemService; // 🛑 YEKUN ƏLAVƏ

    @GetMapping("/dashboard")
    public String viewDashboard(Model model, HttpServletRequest request) {

        // 1. Ümumi Kontrol və Sidebar Aktivliyi
        model.addAttribute("currentUri", request.getRequestURI());

        // 2. STATİSTİK KARTLAR (Dinamik)
        Long totalUsers = userService.countAllUsers();
        model.addAttribute("totalUsers", totalUsers);

        double todayRevenue = orderService.calculateTodayRevenue();
        model.addAttribute("todayRevenue", todayRevenue);

        long todayOrdersCount = orderService.countTodayOrders();
        model.addAttribute("todayOrdersCount", todayOrdersCount);

        long activeProductCount = menuItemService.countActiveMenuItems();
        // Index.html-dəki "activeProductCount" adı ilə uyğunlaşdırırıq
        model.addAttribute("activeProductCount", activeProductCount);


        // 3. ƏN ÇOX SATILAN MƏHSUL (Dinamik)
        String topSellingProductName = "Hələ Sifariş Yoxdur";

        Long topItemId = orderItemService.getTopSellingMenuItemId();

        if (topItemId != null) {
            // Məhsul ID-sini alırıq və Service vasitəsilə adını tapırıq
            MenuItemResponseDTO menuItemDTO = menuItemService.getMenuItemById(topItemId).orElse(null);

            if (menuItemDTO != null) {
                topSellingProductName = menuItemDTO.getName();
            }
        }

        model.addAttribute("topSellingProduct", topSellingProductName);


        // 4. SON 5 SİFARİŞ CƏDVƏLİ (Dinamik)
        List<Order> recentOrders = orderService.getRecentOrders(5);
        model.addAttribute("recentOrders", recentOrders);


        return "dashboard/index";
    }
}