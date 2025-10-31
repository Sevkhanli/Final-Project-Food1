package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.models.Order;
import az.edu.itbrains.food.services.IOrderService;
import az.edu.itbrains.food.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // IZAFI İMPORT

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final IUserService userService;
    private final IOrderService orderService;

    // İndi sadəcə /dashboard ünvanından işləyəcək
    @GetMapping("/dashboard")
    public String viewDashboard(Model model, HttpServletRequest request) { // HttpServletRequest əlavə edildi

        // currentUri dəyişənini View-a ötürürük, dəyəri /dashboard olacaq
        model.addAttribute("currentUri", request.getRequestURI());

        // ... (Mövcud Statistika Kodları) ...
        Long totalUsers = userService.countAllUsers();
        model.addAttribute("totalUsers", totalUsers);

        double todayRevenue = orderService.calculateTodayRevenue();
        model.addAttribute("todayRevenue", todayRevenue);

        long todayOrdersCount = orderService.countTodayOrders();
        model.addAttribute("todayOrdersCount", todayOrdersCount);

        List<Order> recentOrders = orderService.getRecentOrders(5);
        model.addAttribute("recentOrders", recentOrders); // HTML-ə göndərilir

        return "dashboard/index";
    }
}