package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest; // <-- Bu importu əlavə edin

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final IOrderService orderService;

    @GetMapping
    public String listOrders(Model model, HttpServletRequest request) { // <-- HttpServletRequest əlavə edildi

        // ⭐ YENİ ƏLAVƏ: Layout üçün currentUri dəyişənini Model-ə əlavə edirik
        // Thymeleaf-dəki "currentUri.equals('/dashboard')" xətasını həll edir.
        model.addAttribute("currentUri", request.getRequestURI());

        // Sifarişləri DTO formatında gətiririk
        List<OrderListDTO> orders = orderService.getAllOrdersForAdminList();

        model.addAttribute("orders", orders);

        return "dashboard/order-list/index";
    }
}