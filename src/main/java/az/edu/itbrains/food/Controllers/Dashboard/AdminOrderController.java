package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminOrderController {

    private final IOrderService orderService;

    // Sifarişlərin Siyahısı metodu
    @GetMapping("/orders")
    public String listOrders(Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());
        List<OrderListDTO> orders = orderService.getAllOrdersForAdminList();
        model.addAttribute("orders", orders);

        return "dashboard/order-list/index";
    }

    /**
     * Sifarişin Detallarına Baxmaq üçün metod (URL: /admin/orders/details/{id})
     * View Yol: dashboard/order-list/order-details <--- DÜZƏLİŞ
     */
    @GetMapping("/admin/orders/details/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());

        // ⭐ Əsas Düzəliş: orderService-dən sifariş detallarını çəkirik
        // Siz OrderDetailDTO və ya oxşar bir DTO istifadə etməlisiniz.
        // Bu metodun orderService-də mövcud olduğunu fərz edirəm.
        OrderDetailDTO orderDetails = orderService.getOrderDetailsById(id);

        model.addAttribute("order", orderDetails); // Sifariş obyektini "order" adı ilə göndəririk
        model.addAttribute("orderId", id);
        return "dashboard/order-list/order-details";
    }

    /**
     * Sifarişin Statusunu Redaktə etmək üçün metod (URL: /admin/orders/edit-status/{id})
     * View Yol: dashboard/order-list/order-status-edit <--- DÜZƏLİŞ
     */
    @GetMapping("/admin/orders/edit-status/{id}")
    public String editOrderStatus(@PathVariable Long id, Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());
        // Service zəngləri burada olmalıdır
        model.addAttribute("orderId", id);

        // Controller indi order-list/order-status-edit.html faylını axtaracaq
        return "dashboard/order-list/order-status-edit";
    }
}