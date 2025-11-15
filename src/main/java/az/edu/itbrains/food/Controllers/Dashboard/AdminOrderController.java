package az.edu.itbrains.food.Controllers.Dashboard;

import az.edu.itbrains.food.DTOs.DashboardDTO.OrderDetailDTO;
import az.edu.itbrains.food.DTOs.DashboardDTO.OrderListDTO;
import az.edu.itbrains.food.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // POST üçün lazımdır
import org.springframework.web.bind.annotation.RequestParam; // Form data üçün lazımdır
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Mesajlar üçün lazımdır
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminOrderController {

    private final IOrderService orderService;

    // Sifarişlərin Siyahısı metodu (URL: /orders) - Sizin mövcud yolla saxlanılıb
    @GetMapping("/orders")
    public String listOrders(Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());
        List<OrderListDTO> orders = orderService.getAllOrdersForAdminList();
        model.addAttribute("orders", orders);

        return "dashboard/order-list/index";
    }

    /**
     * Sifarişin Detallarına Baxmaq üçün metod (URL: /admin/orders/details/{id}) - Sizin mövcud yolla saxlanılıb
     */
    @GetMapping("/admin/orders/details/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());

        OrderDetailDTO orderDetails = orderService.getOrderDetailsById(id);

        if (orderDetails == null) {
            // Sifariş tapılmazsa, siyahı səhifəsinə yönləndiririk (bu hissə sizin idarənizdədir)
            return "redirect:/orders";
        }

        model.addAttribute("order", orderDetails);
        return "dashboard/order-list/order-details";
    }

    /**
     * Sifarişin Statusunu Redaktə etmək üçün səhifəni göstərir (GET) (URL: /admin/orders/edit-status/{id}) - Sizin mövcud yolla saxlanılıb
     */
    @GetMapping("/admin/orders/edit-status/{id}")
    public String editOrderStatus(@PathVariable Long id, Model model, HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());

        OrderDetailDTO orderDetails = orderService.getOrderDetailsById(id);

        if (orderDetails == null) {
            return "redirect:/orders";
        }

        model.addAttribute("order", orderDetails);
        return "dashboard/order-list/order-status-edit";
    }

    /**
     * ⭐ YENİ FUNKSIONALLIQ: Sifarişin Statusunu Yeniləyir (POST)
     * FORM ACTION: /admin/orders/update-status (order-status-edit.html-dən gəlir)
     */
    @PostMapping("/admin/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("newStatus") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Service-də yaratdığımız metodu çağırırıq
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Sifariş statusu uğurla yeniləndi!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Xəta baş verərsə redaktə səhifəsinə yönləndirmə (bu hissəni sizin yönləndirmənizə uyğun qoyuram)
            return "redirect:/admin/orders/edit-status/" + orderId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Statusu yeniləyərkən gözlənilməz xəta baş verdi.");
        }

        // Yeniləmədən sonra sifariş detalları səhifəsinə geri qayıdırıq (bu hissə sizin yönləndirmənizə uyğun qoyulub)
        return "redirect:/admin/orders/details/" + orderId;
    }
}